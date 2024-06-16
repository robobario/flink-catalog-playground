FROM edenhill/kcat:1.7.1
RUN apk add netcat-openbsd
COPY populate.sh /populate.sh
COPY entry.sh /entry.sh
ENTRYPOINT ["/entry.sh"]
