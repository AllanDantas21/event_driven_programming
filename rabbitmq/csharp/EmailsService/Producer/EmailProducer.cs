using EmailsService.Config;
using EmailsService.Model;
using RabbitMQ.Client;
using System.Text.Json;

namespace EmailsService.Producer;

public class EmailProducer : IDisposable
{
    private readonly IConnection _connection;
    private readonly IModel _channel;
    private readonly ILogger<EmailProducer> _logger;

    public EmailProducer(ILogger<EmailProducer> logger)
    {
        _logger = logger;
        
        var factory = new ConnectionFactory
        {
            HostName = "localhost",
            Port = 5672,
            UserName = "guest",
            Password = "guest"
        };

        _connection = factory.CreateConnection();
        _channel = _connection.CreateModel();
        
        RabbitMQConfig.ConfigureRabbitMQ(_channel);
    }

    public void SendEmail(EmailMessage message)
    {
        try
        {
            _logger.LogInformation("Sending email to queue: {Message}", JsonSerializer.Serialize(message));

            var body = RabbitMQConfig.SerializeMessage(message);

            _channel.BasicPublish(
                exchange: RabbitMQConfig.EmailExchange,
                routingKey: RabbitMQConfig.EmailRoutingKey,
                basicProperties: null,
                body: body
            );

            _logger.LogInformation("Email sent successfully! ID: {Id}", message.Id);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error sending email: {Message}", ex.Message);
            throw new Exception("Failed to send email", ex);
        }
    }

    public void Dispose()
    {
        _channel?.Close();
        _connection?.Close();
        _channel?.Dispose();
        _connection?.Dispose();
    }
}

