using RabbitMQ.Client;
using System.Text.Json;

namespace EmailsService.Configuration;

public static class RabbitMQConfig
{
    public const string EmailQueue = "email.queue";
    public const string EmailExchange = "email.exchange";
    public const string EmailRoutingKey = "email.routing.key";
    
    public const string DlqQueue = "email.dlq";
    public const string DlqExchange = "email.dlq.exchange";
    public const string DlqRoutingKey = "email.dlq.routing.key";

    public static void ConfigureRabbitMQ(IModel channel)
    {
        channel.ExchangeDeclare(EmailExchange, ExchangeType.Direct, durable: true);
        channel.ExchangeDeclare(DlqExchange, ExchangeType.Direct, durable: true);
        channel.QueueDeclare(DlqQueue, durable: true, exclusive: false, autoDelete: false);

        channel.QueueBind(DlqQueue, DlqExchange, DlqRoutingKey);

        var queueArgs = new Dictionary<string, object>
        {
            { "x-dead-letter-exchange", DlqExchange },
            { "x-dead-letter-routing-key", DlqRoutingKey }
        };

        channel.QueueDeclare(EmailQueue, durable: true, exclusive: false, autoDelete: false, queueArgs);

        channel.QueueBind(EmailQueue, EmailExchange, EmailRoutingKey);
    }

    public static byte[] SerializeMessage<T>(T message)
    {
        var json = JsonSerializer.Serialize(message);
        return System.Text.Encoding.UTF8.GetBytes(json);
    }

    public static T? DeserializeMessage<T>(byte[] body)
    {
        var json = System.Text.Encoding.UTF8.GetString(body);
        return JsonSerializer.Deserialize<T>(json);
    }
}