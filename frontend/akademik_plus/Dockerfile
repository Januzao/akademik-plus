FROM node:22-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci
# Source code is mounted as a volume — no COPY src needed
EXPOSE 5173
CMD ["npm", "run", "dev"]
