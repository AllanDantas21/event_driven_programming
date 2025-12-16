using EmailsService.DTOs;
using EmailsService.Models;
using EmailsService.Services;
using Microsoft.AspNetCore.Mvc;

namespace EmailsService.Controllers;

[ApiController]
[Route("api/[controller]")]
public class EmailController : ControllerBase
{
    private readonly EmailProducer _emailProducer;
    private readonly ILogger<EmailController> _logger;

    public EmailController(EmailProducer emailProducer, ILogger<EmailController> logger)
    {
        _emailProducer = emailProducer;
        _logger = logger;
    }

    [HttpPost("confirmation")]
    public IActionResult SendConfirmation([FromBody] EmailRequest request)
    {
        var message = new EmailMessage(
            EmailType.Confirmation,
            request.Recipient,
            "Confirm your registration",
            "Click on the link to confirm: http://example.com/confirm"
        );

        _emailProducer.SendEmail(message);
        return Ok("Confirmation email sent to queue!");
    }

    [HttpPost("welcome")]
    public IActionResult SendWelcome([FromBody] EmailRequest request)
    {
        var message = new EmailMessage(
            EmailType.Welcome,
            request.Recipient,
            "Welcome!",
            "Hello! Welcome to our platform!"
        );

        _emailProducer.SendEmail(message);
        return Ok("Welcome email sent to queue!");
    }

    [HttpPost("password-recovery")]
    public IActionResult SendPasswordRecovery([FromBody] EmailRequest request)
    {
        var message = new EmailMessage(
            EmailType.PasswordRecovery,
            request.Recipient,
            "Password recovery",
            "Use this code to recover your password: 123456"
        );

        _emailProducer.SendEmail(message);
        return Ok("Password recovery email sent to queue!");
    }
}

