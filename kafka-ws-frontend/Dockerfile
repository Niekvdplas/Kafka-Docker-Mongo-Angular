FROM node:18-alpine as build

RUN mkdir /webapp
WORKDIR /webapp

COPY package.json package-lock.json ./

COPY . .
RUN npm install --legacy-peer-deps

CMD ["npm", "start"]