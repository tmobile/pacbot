FROM openjdk:8
ENV RUN_ARGS="--server.port=80 --server.ssl.enabled=false"
RUN cd /tmp/ && curl -O https://bootstrap.pypa.io/get-pip.py && python get-pip.py && pip install awscli && cd -
COPY entrypoint.sh ./entrypoint.sh
RUN chmod +x ./entrypoint.sh
#ENTRYPOINT ["./entrypoint.sh"]

