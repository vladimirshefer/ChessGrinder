FROM node:16-slim as build-app
WORKDIR /app
COPY package.json package.json
RUN npm install
COPY . .
RUN npm run build

FROM nginx:stable-alpine as serve-app
COPY nginx.conf /etc/nginx/conf.d/nginx.conf
COPY --from=build-app /app/build/ /usr/share/nginx/html/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
