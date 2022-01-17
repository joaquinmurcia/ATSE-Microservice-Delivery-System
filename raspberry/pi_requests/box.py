from logging import NullHandler, exception
import requests
from requests import Session
from enum import Enum
from time import sleep

hostname = "localhost"
port = 9000

auth_url = " http://" + hostname + ":" + str(port) + "/usermanagement"
delivery_url = " http://" + hostname + ":" + str(port) + "/deliverymanagement"
box_url = " http://" + hostname + ":" + str(port) + "/boxmanagement"


class Box_status(Enum):
    AVAILABLE = 0
    OCCUPIED = 1
    IN_PROGRESS = 2


class Box:
    box_id = "box1"
    status = Box_status.AVAILABLE
    deliveries = []


    __deliverer_token = None
    __customer_token = None

    def set_new_delivery(self, delivery_token, customer_token):
        self.__deliverer_token = delivery_token
        self.__customer_token = customer_token
        self.status = Box_status.IN_PROGRESS
        return

    def __delivered(self):
        self.status = Box_status.OCCUPIED
        return

    def __picked_up(self):
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


# def createProject(content, xsrf_token):
#     r = httpRequest(
#         "POST ",
#         hostUrl + "/project",
#         params,
#         # 5 . INCLUDE THE BASE HEADERS
#         # 6 . ADD THE REQUEST BODY
#     )

#     print(" S t a t u s code i n s e r t p r o j e c t ", r.status_code)
#     # 7 . CHECK RESPONSE STATUS AND RETURN PROJECTS OR THROW AN EXCEPTION

def get_deliveries():
    data = {}
    data["targetBox"] = "box1"

    r = httpRequest("GET ", delivery_url + "/deliveries", params, content=data)
    if r.status_code != 200:
        raise Exception("Could not get deliveries (status code: " + str(r.status_code) + ")")
    return r.content

def get_my_deliveries():
    data = {}
    data["targetBox"] = "box1"

    r = httpRequest("GET ", box_url + "/boxes" + me.box_id, params, content=data)
    if r.status_code != 200:
        raise Exception("Could not get my deliveries (status code: " + str(r.status_code) + ")")
    return r.content

def get_box():
    r = httpRequest(
        "GET ",
        box_url + "/boxes/" + me.box_id,
        params,
    )
    if r.status_code != 200:
        raise Exception("Could not get box (status code: " + str(r.status_code) + ")")
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
        # me.check_for_new_delivery(deliveries)

    except Exception as inst:
        print(inst.args)


update_deliveries()

# while(True):
#     update_deliveries()
#     sleep(1)
