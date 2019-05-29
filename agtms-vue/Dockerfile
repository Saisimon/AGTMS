FROM node:10
LABEL authors="Saisimon"
COPY ./ /app
WORKDIR /app
RUN npm install && npm run build

FROM nginx:1.15.12-alpine
RUN mkdir /app
COPY waiting.sh /app/waiting.sh
COPY --from=0 /app/dist /app