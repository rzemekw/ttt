FROM nginx:1.21.1-alpine as base

COPY nginx.conf/ /etc/nginx/nginx.conf
#COPY frontend/nginx-dev.conf/ /etc/nginx/nginx.conf
RUN rm /etc/nginx/conf.d/default.conf

WORKDIR /usr/share/nginx/html

COPY dist/* .
EXPOSE 80