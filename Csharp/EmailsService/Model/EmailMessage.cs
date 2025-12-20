using System.Text.Json.Serialization;

namespace EmailsService.Model;

public class EmailMessage
{
    public string Id { get; set; } = Guid.NewGuid().ToString();
    
    [JsonConverter(typeof(JsonStringEnumConverter))]
    public EmailType Type { get; set; }
    
    public string Recipient { get; set; } = string.Empty;
    
    public string Subject { get; set; } = string.Empty;
    
    public string Body { get; set; } = string.Empty;
    
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    public EmailMessage() { }

    public EmailMessage(EmailType type, string recipient, string subject, string body)
    {
        if (string.IsNullOrWhiteSpace(recipient))
            throw new ArgumentException("Recipient não pode ser vazio", nameof(recipient));

        Type = type;
        Recipient = recipient;
        Subject = subject;
        Body = body;
    }
}

