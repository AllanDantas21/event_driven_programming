using EmailsService.Configuration;
using EmailsService.Models;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using System.Text;
using System.Text.Json;

namespace EmailsService.Services;

public class EmailConsumer : BackgroundService
{
    private readonly IConnection _connection;
    private readonly IModel _channel;
    private readonly ILogger<EmailConsumer> _logger;
    private readonly Random _random = new();

    public EmailConsumer(ILogger<EmailConsumer> logger)
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

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        var emailConsumer = new EventingBasicConsumer(_channel);
        emailConsumer.Received += async (model, ea) =>
        {
            var body = ea.Body.ToArray();
            var message = RabbitMQConfig.DeserializeMessage<EmailMessage>(body);

            if (message != null)
            {
                await ProcessEmail(message, ea.DeliveryTag);
            }
        };

        _channel.BasicConsume(
            queue: RabbitMQConfig.EmailQueue,
            autoAck: false,
            consumer: emailConsumer
        );

        var dlqConsumer = new EventingBasicConsumer(_channel);
        dlqConsumer.Received += (model, ea) =>
        {
            var body = ea.Body.ToArray();
            var message = RabbitMQConfig.DeserializeMessage<EmailMessage>(body);

            if (message != null)
            {
                ProcessFailedEmail(message);
            }

            _channel.BasicAck(ea.DeliveryTag, false);
        };

        _channel.BasicConsume(
            queue: RabbitMQConfig.DlqQueue,
            autoAck: false,
            consumer: dlqConsumer
        );

        while (!stoppingToken.IsCancellationRequested)
        {
            await Task.Delay(1000, stoppingToken);
        }
    }

    private async Task ProcessEmail(EmailMessage message, ulong deliveryTag)
    {
        _logger.LogInformation("Processing email: {Message}", JsonSerializer.Serialize(message));

        try
        {
            await Task.Delay(_random.Next(800, 1200));

                // Simulando falha (30% de chance)
            if (ShouldSimulateFailure())
            {
                throw new Exception("Simulated failure sending email");
            }

            switch (message.Type)
            {
                case EmailType.Confirmation:
                    SendConfirmationEmail(message);
                    break;
                case EmailType.Welcome:
                    SendWelcomeEmail(message);
                    break;
                case EmailType.PasswordRecovery:
                    SendPasswordRecoveryEmail(message);
                    break;
            }

            _logger.LogInformation("Email sent successfully: {Message}", JsonSerializer.Serialize(message));
            _channel.BasicAck(deliveryTag, false);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error processing email: {Message}", ex.Message);
            
            _channel.BasicNack(deliveryTag, false, false);
        }
    }

    private void SendConfirmationEmail(EmailMessage message)
    {
        _logger.LogInformation("Confirmation sending to: {Recipient}", message.Recipient);
        _logger.LogInformation("   Subject: {Subject}", message.Subject);
        _logger.LogInformation("   Body: {Body}", message.Body);
    }

    private void SendWelcomeEmail(EmailMessage message)
    {
        _logger.LogInformation("Welcome sending to: {Recipient}", message.Recipient);
        _logger.LogInformation("   Subject: {Subject}", message.Subject);
        _logger.LogInformation("   Body: {Body}", message.Body);
    }

    private void SendPasswordRecoveryEmail(EmailMessage message)
    {
        _logger.LogInformation("Password recovery sending to: {Recipient}", message.Recipient);
        _logger.LogInformation("   Subject: {Subject}", message.Subject);
        _logger.LogInformation("   Body: {Body}", message.Body);
    }

    private bool ShouldSimulateFailure()
    {
        return _random.Next(100) < 30;
    }

    private void ProcessFailedEmail(EmailMessage message)
    {
        _logger.LogError("Email went to DLQ after 3 failed attempts!");
        _logger.LogError("   ID: {Id}", message.Id);
        _logger.LogError("   Type: {Type}", message.Type);
        _logger.LogError("   Recipient: {Recipient}", message.Recipient);
    }

    public override void Dispose()
    {
        _channel?.Close();
        _connection?.Close();
        base.Dispose();
    }
}

