FROM node:10 as build
LABEL authors="Saisimon"
COPY ./ /app
WORKDIR /app
RUN npm install --registry=https://registry.npm.taobao.org && npm run build

FROM nginx:1.15.12-alpine
RUN mkdir /app
COPY waiting.sh /app/waiting.sh
RUN chmod 777 /app/waiting.sh
COPY --from=build /app/dist /app