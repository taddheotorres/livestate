FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration production

FROM nginx:alpine
RUN rm -rf /usr/share/nginx/html/*
COPY nginx.template.conf /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist/frontend /usr/share/nginx/html
EXPOSE 80
