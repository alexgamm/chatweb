FROM nginx

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY dist /opt/chatweb/frontend

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]