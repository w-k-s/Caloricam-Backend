up:
	mvn clean package -U; docker compose up -d --build

kill:
	docker-compose down --rmi all

logs:
	docker logs -f app