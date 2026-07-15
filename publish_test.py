import pika, json, sys
conn = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
ch = conn.channel()
ch.exchange_declare(exchange='techcup.exchange', exchange_type='topic', durable=True)
ch.queue_declare(queue='techcup.statistics.match-events', durable=True)
ch.queue_bind(exchange='techcup.exchange', queue='techcup.statistics.match-events', routing_key='techcup.match.event.#')

event = {'playerId':'p-local-1','teamId':'t-local-1','matchId':'m-local-1','tournamentId':'tn-local-1','result':'WON','goals':2,'yellowCards':1,'redCards':0,'foulsCommitted':3,'minutesPlayed':90,'assists':1,'goalkeeper':False}
ch.basic_publish(exchange='techcup.exchange', routing_key='techcup.match.event.stat', body=json.dumps(event))
print('Evento publicado correctamente en RabbitMQ local')
conn.close()
