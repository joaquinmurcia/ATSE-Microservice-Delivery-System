from logging import NullHandler, exception
import requests
from requests import Session
import json
from enum import Enum
import traceback
import threading, time

hostname = "10.42.0.1" #"localhost"
port = 9000

auth_url = " http://" + hostname + ":" + str(port) + "/usermanagement"
delivery_url = " http://" + hostname + ":" + str(port) + "/deliverymanagement"
box_url = " http://" + hostname + ":" + str(port) + "/boxmanagement"


jwt = ""

# Use session so we don't have to rewrite the cookies and JWT for every request
session = requests.Session()

params = {
    "mode": "cors",
    "cache": "no-cache",
    "credentials": "include",
    "redirect": "follow",
    "referrerPolicy": "origin-when-cross-origin",
}


class Box_status(Enum):
    AVAILABLE = 0
    OCCUPIED = 1
    IN_PROGRESS = 2
    ERROR = 3


class Box:
    box_id = "targetBox1"
    status = Box_status.AVAILABLE
    deliveries = []
    __deliverer_tokens = []
    __customer_token = None

    def __new_delivery(self, delivery):
        
        info = get_delivery_info(delivery["deliveryID"])
        info = response_to_json(info)
        new_customer_token = info["targetCustomerRFIDToken"]
        new_deliverer_token = info["responsibleDelivererRfidToken"]

        if self.__customer_token != None and self.__customer_token != new_customer_token:  # sanity check
            raise Exception("New customer assigned even though one already exists")

        self.__deliverer_tokens.append(new_deliverer_token)
        self.__customer_token = new_customer_token
        self.status = Box_status.IN_PROGRESS
        self.deliveries.append(delivery)
        return

    def __delivered(self, token):
        index = self.__deliverer_tokens.index(token)
        self.deliveries[index]["deliveryStatus"] = "delivered"
        delivery = self.deliveries[index]
        ret = set_delivery_delivered(delivery["deliveryID"])

        self.__deliverer_tokens.remove(token)

        self.status = Box_status.OCCUPIED
        return

    def __picked_up(self):
        for delivery in self.deliveries:
            set_delivery_picked_up(delivery["deliveryID"])
                
        
        # status: pickedUp
        self.status = Box_status.AVAILABLE
        self.__customer_token = None
        self.deliveries = []
        return

    def __open(self,pi_stuff):
        pi_stuff.blink_green()
        
        # Wait for photo_sensor to turn to 1 = Box is open
        while(pi_stuff.get_brightness() != 1):
            pass
        print("OPENED BOX")

        start = time.time()
        time.sleep(1)
        # Wait for photo_sensor to turn to 0 = Box is closed

        wait_time = 10
        while(pi_stuff.get_brightness() != 0):
            end = time.time()
            if (end - start > wait_time):
                pi_stuff.blink_red_short()
                time.sleep(.5)
        print("Closed BOX")

        return

    def request_open(self, token, pi_stuff):
        print("open_requested")
        ret = False

        if self.status == Box_status.IN_PROGRESS:
            if token in self.__deliverer_tokens:
                self.__open(pi_stuff)
                self.__delivered(token)
                ret = True

        if self.status == Box_status.OCCUPIED:
            if self.__customer_token == token:
                self.__open(pi_stuff)
                self.__picked_up()
                ret = True

        return ret
    
    def _get_delivery_by_id(self, delivery_id):
        for active_delivery in self.deliveries:
            if active_delivery["deliveryID"] == delivery_id:
                return active_delivery
        return ""



    def check_new_deliveries(self, deliveries):
        for incoming_delivery in deliveries:
            if self._get_delivery_by_id(incoming_delivery["deliveryID"]) == "":
                    self.__new_delivery(incoming_delivery)
            else:
                pass

    def list_current_deliveries(self):
        if len(self.deliveries) == 0:
            print("No active deliveries")
            return
        else:
            print("Active Deliveries: ")
            for delivery in self.deliveries:
                print(delivery)
                print()

me = Box()



def httpRequest(method, url, params, headers=" ", content=" ", auth=" "):
    if method == "GET ":
        res = session.get(url, params=params, json=content)
        return res
    elif method == "POST ":
        if auth == " ":
            res = session.post(url, params=params, headers=headers, json=content)
        else:
            res = session.post(url, params=params, headers=headers, auth=auth)
            return res
    else:
        raise ValueError(" Method Not Found ")


def httpRequest_customHeader(method, url, params, headers=" ", content=" ", auth=" "):
    if method == "GET ":
        res = session.get(url, params=params, json=content, headers=headers)
        return res
    elif method == "POST ":
        if auth == " ":
            res = session.post(url, params=params, headers=headers, json=content)
        else:
            res = session.post(url, params=params, headers=headers, auth=auth)
            return res
    elif method == "PUT ":
        res = session.put(url, params=params, json=content, headers=headers)
        return res
    else:
        raise ValueError(" Method Not Found ")



# def getBaseHeaders(xsrf_token):
#     return {"Content-Type": "application/json", "X-XSRF-TOKEN": xsrf_token}

def getBaseHeaders(jwt=""):
    ret = {}
    ret["Content-Type"] = "application/json"
    if jwt != "":
        ret["Cookie"] =  "jwt=" + jwt

    return  ret

def response_to_json(response_bytes):
    response_string = response_bytes.decode('utf8').replace("'", '"')
    response = json.loads(response_string)
    return response


def getXSRFToken():
    r = httpRequest("GET ", auth_url + "/auth", params)
    if r.status_code == 503:
        raise Exception("Failed to get Auth - status code: ", r.status_code)
    return r.cookies


def get_jwt(username, password):
    r = httpRequest("POST ", auth_url + "/auth", params=params, auth=(username, password), headers = getBaseHeaders())

    if r.status_code != 200:
        raise Exception("Could not get JWT token (status code: " + str(r.status_code) + ")")
    return r.cookies


# Box stuff


def get_my_box_info():
    r = httpRequest(
        "GET ",
        box_url + "/boxes/" + me.box_id,
        params,
    )
    if r.status_code != 200:
        raise Exception("Could not get box (status code: " + str(r.status_code) + ")")
    return r.content


def get_my_deliveries():
    r = httpRequest_customHeader("GET ", box_url + "/boxes/" + me.box_id, params, headers=getBaseHeaders(jwt))
    return r.content


def get_delivery_info(delivery):
    r = httpRequest_customHeader("GET ", delivery_url + "/deliveries/" + str(delivery), params, headers=getBaseHeaders(jwt))
    return r.content


def get_deliveries():
    data = {}

    r = httpRequest("GET ", delivery_url + "/deliveries", params, content=data)

    if r.status_code != 200:
        raise Exception("Could not get deliveries (status code: " + str(r.status_code) + ")")
    return r.content


def set_delivery_delivered(delivery):
    data = {}
    data["deliveryStatus"] = "delivered"

    r = httpRequest_customHeader("PUT ", delivery_url + "/deliveries/" + str(delivery), params, content=data,headers=getBaseHeaders(jwt))
    if r.status_code != 200:
        raise Exception("Failed to set delivery id " + delivery["deliveryID"] + " to delivered (status code: " + str(r.status_code) + ")")

    return r


def set_delivery_picked_up(delivery):
    data = {}
    data["deliveryStatus"] = "pickedUp"

    r = httpRequest_customHeader("PUT ", delivery_url + "/deliveries/" + str(delivery), params, content=data,headers=getBaseHeaders(jwt))
    if r.status_code != 200:
        raise Exception("Failed to set delivery id " + delivery["deliveryID"] + " to picked-up (status code: " + str(r.status_code) + ")")

    return r


# Cookies will automatically be stored by python
ret_cookies = getXSRFToken()

ret_cookies = get_jwt("User3", "pwd3")

for c in ret_cookies:
    if c.name == "jwt":
        jwt = c.value

def update_deliveries():
    try:
        deliveries_bytes = get_my_deliveries()
        deliveries = response_to_json(deliveries_bytes)    
        if deliveries_bytes == b'' and me.deliveries != []:  # sanity check
            raise Exception("Received deliveries empty but current delivery is not done.")
        deliveries = [deliveries] # NOTE: Cast to arrays as long as there is only one delivery

        me.check_new_deliveries(deliveries)
        me.list_current_deliveries()

    except Exception as inst:
        print(traceback.format_exc())
        print(inst.args)