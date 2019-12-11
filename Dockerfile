FROM circleci/openjdk:8-node

USER root

WORKDIR /aiad

COPY . ./

RUN echo "Compiling!"
RUN ./compile.sh
RUN echo "Compiled!"

CMD [ "node", "create_dataset.js" ]
# CMD [ "/bin/bash" ]
