from logging import NullHandler, exception
import requests
from requests import Session
import json
from enum import Enum
import traceback
import yaml
import threading, time

# hostname = "10.42.0.1"
hostname = "localhost"
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


class Box:
    box_id = "targetBox1"
    name = ""
    address = ""
    deliveries = []
    __deliverer_tokens = []
    __customer_token = None

    def __init__(self,box_id, name, address):
        """ Create a new point at the origin """
        self.box_id = box_id
        self.name = name
        self.address = address
        self.deliveries = []
        self.__deliverer_tokens = []
        self.__customer_token = None

    def info(self):
        print("ID: " + self.box_id)
        print( "Name: " + str(self.name))
        print("Address: " + str(self.address))
        print("-"*24)
        print("deliveries:")
        for delivery in self.deliveries:
            print(delivery)
            print()
        print("-"*24)
        print("__deliverer_tokens: " + str(self.__deliverer_tokens))
        print("__customer_token: " + str(self.__customer_token))

    def __new_delivery(self, delivery):
        new_customer_token = delivery["targetCustomerRFIDToken"]
        new_deliverer_token = delivery["responsibleDelivererRfidToken"]

        if self.__customer_token != None and self.__customer_token != new_customer_token:  # sanity check
            raise Exception("New customer assigned even though one already exists")

        if new_deliverer_token not in self.__deliverer_tokens:
            self.__deliverer_tokens.append(new_deliverer_token)
            
        self.__customer_token = new_customer_token
        self.deliveries.append(delivery)
        return

    def _check_delivery_updated(self, delivery):
        old_delivery = self._get_delivery_by_id(delivery["id"])
        
        if delivery["targetCustomer"] != old_delivery["targetCustomer"]:
            old_delivery["targetCustomer"] = delivery["targetCustomer"]

        if delivery["targetCustomerRFIDToken"] != old_delivery["targetCustomerRFIDToken"]:
            old_delivery["targetCustomerRFIDToken"] = delivery["targetCustomerRFIDToken"]

        if delivery["responsibleDeliverer"] != old_delivery["responsibleDeliverer"]:
            old_delivery["responsibleDeliverer"] = delivery["responsibleDeliverer"]

        if delivery["responsibleDelivererRfidToken"] != old_delivery["responsibleDelivererRfidToken"]:
            old_delivery["responsibleDelivererRfidToken"] = delivery["responsibleDelivererRfidToken"]

        if delivery["deliveryStatus"] != old_delivery["deliveryStatus"]:
            old_delivery["deliveryStatus"] = delivery["deliveryStatus"]

        if delivery["valid"] != old_delivery["valid"]:
            old_delivery["valid"] = delivery["valid"]

    def __is_deliverer_active(self, deliverer_token):
        for active_delivery in self.deliveries:
            if active_delivery["deliveryStatus"] == "open":
                if active_delivery["responsibleDelivererRfidToken"] == deliverer_token:
                    return True
        return False

    def __delivered(self, token):
        for delivery in self.deliveries:
            if delivery["deliveryStatus"] != "pickedUp":
                if delivery["responsibleDelivererRfidToken"] == token:
                    delivery["deliveryStatus"] = "delivered"
                    #set_delivery_delivered(delivery["id"])

        if(not self.__is_deliverer_active(token)):
            self.__deliverer_tokens.remove(token)

        return

    def __ready_for_pickup(self):
        ret = False
        for active_delivery in self.deliveries:
            if active_delivery["deliveryStatus"] == "delivered":
                ret = True

        if ret == False:
            print("No deliveries ready for pickup!")
        return ret

    def __get_active_deliveries(self):
        ret = []
        for active_delivery in self.deliveries:
            if active_delivery["deliveryStatus"] != "pickedUp":
                ret.append(active_delivery)
        return ret


    def __picked_up(self):
        r = pick_up_all()
        if r.status_code != 200:
            return

        for delivery in self.deliveries:
            if delivery["deliveryStatus"] == "delivered":
                #set_delivery_picked_up(delivery["id"])
                delivery["deliveryStatus"] = "pickedUp"

        if self.__get_active_deliveries() == []:
            self.__customer_token = None

        return

    def __open(self, pi_stuff):
        pi_stuff.blink_green()

        # Wait for photo_sensor to turn to 1 = Box is open
        while pi_stuff.get_brightness() != 1:
            pass
        print("OPENED BOX")

        start = time.time()
        time.sleep(1)
        # Wait for photo_sensor to turn to 0 = Box is closed

        wait_time = 10
        while pi_stuff.get_brightness() != 0:
            end = time.time()
            if end - start > wait_time:
                pi_stuff.blink_red_short()
                time.sleep(0.5)
        print("Closed BOX")

        return

    def request_open(self, token, pi_stuff):
        print("open_requested")
        ret = False

        if token in self.__deliverer_tokens:
            self.__open(pi_stuff)
            self.__delivered(token)
            ret = True

        if self.__customer_token == token:
            if self.__ready_for_pickup():
                self.__open(pi_stuff)
                self.__picked_up()
                ret = True

        return ret

    def _get_delivery_by_id(self, delivery_id):
        for active_delivery in self.deliveries:
            if active_delivery["id"] == delivery_id:
                return active_delivery
        return ""

    def check_new_deliveries(self, deliveries):
        for incoming_delivery in deliveries:
            if self._get_delivery_by_id(incoming_delivery["id"]) == "":

                # Ignore completed deliveries
                if incoming_delivery["deliveryStatus"] == "pickedUp":
                    continue

                self.__new_delivery(incoming_delivery)
            else:
                self._check_delivery_updated(incoming_delivery)
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


me = None

with open("config.yaml", "r") as stream:
    tmp = yaml.safe_load(stream)
    print(tmp)
    me = Box(tmp["box_id"], tmp["name"], tmp["address"])


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
        ret["Cookie"] = "jwt=" + jwt

    return ret


def response_to_json(response_bytes):
    if response_bytes == b"":
        return []
    response_string = response_bytes.decode("utf8").replace("'", '"')
    response = json.loads(response_string)
    return response


def getXSRFToken():
    r = httpRequest("GET ", auth_url + "/auth", params)
    if r.status_code == 503:
        raise Exception("Failed to get Auth - status code: ", r.status_code)
    return r.cookies


def get_jwt(username, password):
    r = httpRequest("POST ", auth_url + "/auth", params=params, auth=(username, password), headers=getBaseHeaders())

    if r.status_code != 200:
        raise Exception("Could not get JWT token (status code: " + str(r.status_code) + ")")
    return r.cookies


# Box stuff


# def get_my_box_info():
#     r = httpRequest(
#         "GET ",
#         box_url + "/boxes/" + me.box_id,
#         params,
#     )
#     if r.status_code != 200:
#         raise Exception("Could not get box (status code: " + str(r.status_code) + ")")
#     return r.content


def get_my_deliveries():
    r = httpRequest_customHeader("GET ", delivery_url + "/deliveries?boxId=" + str(me.box_id), params, headers=getBaseHeaders(jwt))
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


# def set_delivery_delivered(delivery):
#     # DONE BY THE DELIVERER THROUGH THE FRONTEND
#     pass
#     # data = {}
#     # data["deliveryStatus"] = "delivered"
#     # # TODO: /{id}/deposit
#     # r = httpRequest_customHeader("PUT ", delivery_url + "/" + str(delivery) + "/deposit", params, content=data, headers=getBaseHeaders(jwt))

#     # # r = httpRequest_customHeader("PUT ", delivery_url + "/deliveries/" + str(delivery), params, content=data, headers=getBaseHeaders(jwt))
#     # if r.status_code != 200:
#     #     raise Exception("Failed to set delivery id " + delivery["id"] + " to delivered (status code: " + str(r.status_code) + ")")

#     # return r

def pick_up_all():
    data = {}
    data["deliveryStatus"] = "pickedUp"
    r = httpRequest_customHeader("PUT ", box_url  + "/boxes/" +  str(me.box_id) + "/pickupDeliveries" , params, content=data, headers=getBaseHeaders(jwt))
    if r.status_code != 200:
        raise Exception("Failed tell server that all deliveries were picked up (status code: " + str(r.status_code) + ")")

    return r


# def set_delivery_picked_up(delivery):
#     data = {}
#     data["deliveryStatus"] = "pickedUp"

#     r = httpRequest_customHeader("PUT ", delivery_url + "/deliveries/" + str(delivery), params, content=data, headers=getBaseHeaders(jwt))
#     if r.status_code != 200:
#         raise Exception("Failed to set delivery id " + delivery["id"] + " to picked-up (status code: " + str(r.status_code) + ")")

#     return r


# Cookies will automatically be stored by python
ret_cookies = getXSRFToken()

ret_cookies = get_jwt("Dispatcher", "pwd3")

for c in ret_cookies:
    if c.name == "jwt":
        jwt = c.value


def update_deliveries():
    try:
        deliveries_bytes = get_my_deliveries()
        if deliveries_bytes == b"" and me.deliveries != []:  # sanity check
            raise Exception("Received deliveries empty but current delivery is not done. Resetting...")
            # TODO: REset box
        deliveries = response_to_json(deliveries_bytes)
        deliveries = deliveries

        me.check_new_deliveries(deliveries)
        #me.list_current_deliveries()

    except Exception as inst:
        print(traceback.format_exc())
        print(inst.args)
