public abstract class Command {
}

class MessageCommand extends Command {
    String message;

    MessageCommand(String message) {
        this.message = message;
    }
}

class DisconnectCommand extends Command {
}

class NickCommand extends Command {
    String nick;
    boolean busy;

    NickCommand(String nick, boolean busy) {
        this.nick = nick;
        this.busy = busy;
    }
}

class AcceptCommand extends Command {
}

class RejectCommand extends Command {
}