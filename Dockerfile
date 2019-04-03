FROM maven:3.5.4

## ensure locale is set during build
ENV LANG            C.UTF-8

RUN apt-get update && \
    apt-get install -y --no-install-recommends gnupg ca-certificates dirmngr curl git && \
    echo 'deb http://downloads.haskell.org/debian stretch main' > /etc/apt/sources.list.d/ghc.list && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys BA3CBA3FFE22B574 && \
    apt-get update && \
    apt-get install -y --no-install-recommends ghc-8.2.2 cabal-install-2.2 \
        zlib1g-dev libtinfo-dev libsqlite3-dev g++ netbase xz-utils make && \
    curl -fSL https://github.com/commercialhaskell/stack/releases/download/v1.7.1/stack-1.7.1-linux-x86_64.tar.gz -o stack.tar.gz && \
    curl -fSL https://github.com/commercialhaskell/stack/releases/download/v1.7.1/stack-1.7.1-linux-x86_64.tar.gz.asc -o stack.tar.gz.asc && \
    apt-get purge -y --auto-remove curl && \
    export GNUPGHOME="$(mktemp -d)" && \
    gpg --keyserver ha.pool.sks-keyservers.net --recv-keys C5705533DA4F78D8664B5DC0575159689BEFB442 && \
    gpg --batch --verify stack.tar.gz.asc stack.tar.gz && \
    tar -xf stack.tar.gz -C /usr/local/bin --strip-components=1 && \
    /usr/local/bin/stack config set system-ghc --global true && \
    /usr/local/bin/stack config set install-ghc --global false && \
    rm -rf "$GNUPGHOME" /var/lib/apt/lists/* /stack.tar.gz.asc /stack.tar.gz

ENV PATH /root/.cabal/bin:/root/.local/bin:/opt/cabal/2.2/bin:/opt/ghc/8.2.2/bin:$PATH

WORKDIR /

RUN git clone https://github.com/facebook/duckling.git

RUN mkdir /log

WORKDIR /duckling

RUN apt-get update

RUN apt-get install -qq -y libpcre3 libpcre3-dev build-essential --fix-missing --no-install-recommends

RUN stack setup
# NOTE:`stack build` will use as many cores as are available to build
# in parallel. However, this can cause OOM issues as the linking step
# in GHC can be expensive. If the build fails, try specifying the
# '-j1' flag to force the build to run sequentially.

RUN stack build

#ENTRYPOINT stack exec duckling-example-exe

WORKDIR /

RUN git clone https://ci.linagora.com/zsellami/automatic-email-answering.git

WORKDIR automatic-email-answering/

RUN mvn install -Dmaven.test.skip=true

RUN tar -zxvf talismane-distribution-5.1.2-bin.tar.gz

RUN mkdir bin

WORKDIR /automatic-email-answering/bin

RUN mkdir tmp

WORKDIR /

RUN cp /automatic-email-answering/talismane-distribution-5.1.2-bin/frenchLanguagePack-5.0.4.zip /automatic-email-answering/bin/frenchLanguagePack-5.0.4.zip

RUN cp /automatic-email-answering/target/intentDetection-1.0.jar /automatic-email-answering/bin/intentDetection.jar

RUN cp /automatic-email-answering/intent6.owl /automatic-email-answering/bin/intent6.owl

RUN cp -r /automatic-email-answering/target/lib/ /automatic-email-answering/bin/lib/

RUN cp /automatic-email-answering/CONFIG_DockerFile /automatic-email-answering/bin/CONFIG

RUN cp /automatic-email-answering/firstname_female.lst /automatic-email-answering/bin/firstname_female.lst

RUN cp /automatic-email-answering/firstname_male.lst /automatic-email-answering/bin/firstname_male.lst

RUN cp /automatic-email-answering/TextCleaner.regex /automatic-email-answering/bin/TextCleaner.regex

EXPOSE 9991

CMD  java -jar -Xmx4G -Xms4G -Duser.dir=/automatic-email-answering/bin  /automatic-email-answering/bin/intentDetection.jar /automatic-email-answering/bin/CONFIG
