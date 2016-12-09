public abstract class Command
{
}

class MessageCommand extends Command
{
    String message;

    MessageCommand(String message)
    {
        this.message = message;
    }
}

class NickCommand extends Command {
    String nick;

    NickCommand(String nick)
    {
        this.nick = nick;
    }
}
