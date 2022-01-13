import requests
from requests import Session

hostname = "localhost"
port = 9000
# hostUrl = " http://" + hostname + ":" + str(port)

auth_service = "localhost"
auth_port = 8080
auth_url = " http://" + auth_service + ":" + str(auth_port)


delivery_url = " http://" + hostname + ":" + str(port) + "/deliverymanagement"
box_url = " http://" + hostname + ":" + str(port) + "/boxmanagement"


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
    r = httpRequest("POST ", auth_url + "/auth", params=params, auth=(username,password),headers=getBaseHeaders(xsrf_token))
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

    r = httpRequest(
    "GET ",
    delivery_url + "/deliveries",
    params,    
    content=data
    )
    if r.status_code != 200:
        raise Exception("Could not get deliveries (status code: " + str(r.status_code) + ")")
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
        
deliveries = get_deliveries()
print(deliveries)