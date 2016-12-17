abstract class Command {}

class MessageCommand extends Command {
    String message;

    MessageCommand(String message) {
        this.message = message;
    }
}

class RequestCommand extends Command {
    String nick, IP;

    RequestCommand(String nick, String IP) {
        this.nick = nick;
        this.IP = IP;
    }
}

class RejectCommand extends Command {
    String nick, IP;

    RejectCommand(String nick, String IP) {
        this.nick = nick;
        this.IP = IP;
    }
}

class AcceptCommand extends Command {
    String nick, IP;

    AcceptCommand(String nick, String IP) {
        this.nick = nick;
        this.IP = IP;
    }
}

class DisconnectCommand extends Command {
    String nick, IP;

    DisconnectCommand(String nick, String IP) {
        this.nick = nick;
        this.IP = IP;
    }
}

class BusyCommand extends Command {
    String nick, IP;

    BusyCommand(String nick, String IP) {
        this.nick = nick;
        this.IP = IP;
    }
}