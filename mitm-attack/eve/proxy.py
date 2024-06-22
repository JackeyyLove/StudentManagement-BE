
from mitmproxy import http

def response(flow: http.HTTPFlow) -> None:
    flow.response.content = flow.response.content.decode().replace("Helloooo! I'm an honest website!", "You have been hacked").encode()

