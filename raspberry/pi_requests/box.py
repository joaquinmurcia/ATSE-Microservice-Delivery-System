from logging import NullHandler, exception
import requests
from requests import Session
from enum import Enum
import threading, time

hostname = "localhost"
port = 9000

auth_url = " http://" + hostname + ":" + str(port) + "/usermanagement"
delivery_url = " http://" + hostname + ":" + str(port) + "/deliverymanagement"
box_url = " http://" + hostname + ":" + str(port) + "/boxmanagement"

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
        info = get_delivery_info(delivery)
        # TODO: Not Tested
        new_customer_token = info["customer_token"]
        new_deliverer_token = info["deliverer_token"]

        if self.__customer_token != None and self.__customer_token != new_customer_token: # sanity check
            raise Exception("New customer assigned even though one already exists")
        
        self.__deliverer_tokens.append(new_deliverer_token)
        self.__customer_token = new_customer_token
        self.status = Box_status.IN_PROGRESS
        return

    def __delivered(self):
        set_delivery_delivered()
        self.status = Box_status.OCCUPIED
        return

    def __picked_up(self):
        set_delivery_picked_up()
        self.status = Box_status.AVAILABLE
        self.__deliverer_token = None
        self.__customer_token = None
        return

    def __open(self):
        return

    def request_open(self, token):
        ret = False

        if self.status == Box_status.IN_PROGRESS:
            if self.__deliverer_token == token:
                self.__open()
                self.__delivered()
                ret = True

        if self.status == Box_status.OCCUPIED:
            if self.__customer_token == token:
                self.__open()
                self.__picked_up()
                ret = True

        return ret

    def check_new_deliveries(self, deliveries):
        # TODO not tested
        for delivery in deliveries:
            if delivery not in self.deliveries:
                self.__new_delivery(delivery)


me = Box()

xsrf_token = ""

# Use session so we don't have to rewrite the cookies and JWT for every request
session = requests.Session()

params = {
    "mode": "cors",
    "cache": "no-cache",
    "credentials": "include",
    "redirect": "follow",
    "referrerPolicy": "origin-when-cross-origin",
}


def httpRequest(method, url, params, headers=" ", content=" ", auth=" "):
    if method == "GET ":
        res = session.get(url, params=params,json=content)
        return res
    elif method == "POST ":
        if auth == " ":
            res = session.post(url, params=params, headers=headers, json=content)
        else:
            res = session.post(url, params=params, headers=headers, auth=auth)
            return res
    else:
        raise ValueError(" Method Not Found ")


def getBaseHeaders(xsrf_token):
    return {"Content-Type": "application/json", "X-XSRF-TOKEN": xsrf_token}


def getXSRFToken():
    r = httpRequest("GET ", auth_url + "/auth", params)
    return r.cookies


def get_jwt(username, password):
    auth_stuff = requests.auth.HTTPBasicAuth(username, password)
    r = httpRequest("POST ", auth_url + "/auth", params=params, auth=(username, password), headers=getBaseHeaders(xsrf_token))
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
    data = {}
    data["targetBox"] = me.box_id

    r = httpRequest("GET ", box_url + "/boxes" + me.box_id, params, content=data)
    if r.status_code != 200:
        raise Exception("Could not get my deliveries (status code: " + str(r.status_code) + ")")
    return r.content

def get_delivery_info(delivery):
    data = {}
    data["targetBox"] = me.box_id

    r = httpRequest("GET ", delivery_url + "/deliveries/"+ str(delivery), params, content=data)

    if r.status_code != 200:
        raise Exception("Could not get deliveries (status code: " + str(r.status_code) + ")")
    return r.content

def set_delivery_delivered(delivery):
    data = {}
    data ["id"] = delivery
    data ["targetBox"] = me.box_id
    data ["targetCustomer"] = 2
    data ["targetCustomerRFIDToken"] = 2
    data ["responsibleDeliverer"] = 2
    data ["responsibleDelivererRfidToken"] = 2
    data ["deliveryStatus"] = "delivered"


    r = httpRequest("PUT ", delivery_url + "/deliveries/"+ str(delivery), params, content=data)

    if r.status_code != 200:
        raise Exception("Failed to update delivery status (status code: " + str(r.status_code) + ")")
    return r.content

def set_delivery_picked_up(delivery):
    data = {}
    data ["id"] = delivery
    data ["targetBox"] = me.box_id
    data ["targetCustomer"] = 2
    data ["targetCustomerRFIDToken"] = 2
    data ["responsibleDeliverer"] = 2
    data ["responsibleDelivererRfidToken"] = 2
    data ["deliveryStatus"] = "pickedUp"


    r = httpRequest("PUT ", delivery_url + "/deliveries/"+ str(delivery), params, content=data)

    if r.status_code != 200:
        raise Exception("Failed to update delivery status (status code: " + str(r.status_code) + ")")
    return r.content



# Cookies will automatically be stored by python
ret_cookies = getXSRFToken()

for c in ret_cookies:
    if(c.name == "XSRF-TOKEN"):
        xsrf_token = c.value

if xsrf_token == "":
    raise Exception('Invalid xsrf_token')

ret_cookies = get_jwt("User1","pwd1")

jwt = ""
for c in ret_cookies:
    if(c.name == "jwt"):
        jwt = c.value

print("Active cookies:" + str([c.name for c in session.cookies]))

# new_cookies = []
# for i in range(len(session.cookies)):
#     if(session.cookies[i].name =="XSRF-TOKEN"):
#         print("gottem")

# deliveries = get_deliveries()
# print(deliveries)


def update_deliveries():
    try:
        deliveries = get_my_deliveries()
        # TODO check if new deliveries were made
        if deliveries == [] and me.deliveries != []: # sanity check
            raise Exception("Received no assigned deliveries but current delivery is not over")
        me.check_new_deliveries(deliveries)

    except Exception as inst:
        print(inst.args)


update_deliveries()


# WAIT_TIME_SECONDS = 10

# ticker = threading.Event()
# while True:
#     if not ticker.wait(WAIT_TIME_SECONDS):
#         update_deliveries()
