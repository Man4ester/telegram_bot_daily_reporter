FROM java:8-jre


COPY build/libs/daily-reporter.jar daily-reporter.jar
COPY startup.sh startup.sh

CMD ["bash","startup.sh"]
