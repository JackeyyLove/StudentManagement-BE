FROM ubuntu
ENV TZ=Asia/Vietnam
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN apt-get update && apt-get install -y iptables tcpdump dsniff iproute2 python3 python3-pip tmux dnsutils
RUN rm /usr/lib/python3.*/EXTERNALLY-MANAGED
RUN pip3 install scapy mitmproxy

CMD exec /bin/bash -c "trap : TERM INT; sleep infinity & wait"
