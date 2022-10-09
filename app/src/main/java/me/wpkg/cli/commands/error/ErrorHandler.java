package me.wpkg.cli.commands.error;

import java.util.regex.Pattern;

public class ErrorHandler
{
    private Errors error = Errors.OK;
    private String msg;

    public enum Errors
    {
        OK("[0]"),ERROR("[1]"),SESSION_EXPIRED(""), NOT_AUTHORIZED("[NOT_AUTHORIZED]");
        public final String code;

        public Runnable event = () -> {};
        Errors(String code)
        {
            this.code = code;
        }

        public void executeEvent()
        {
            event.run();
        }
    }

    private Errors byCode(String message)
    {
        if (message.startsWith(Errors.OK.code))
            return Errors.OK;
        else if (message.startsWith(Errors.ERROR.code))
            return Errors.ERROR;
        else if (message.startsWith(Errors.NOT_AUTHORIZED.code))
            return Errors.NOT_AUTHORIZED;
            //parse serverd message about not joined
        else if (message.startsWith("Client ") && message.contains("not joined. Unknown command."))
            return Errors.SESSION_EXPIRED;
        return Errors.OK;
    }

    public void setSessionExpiredEvent(Runnable runnable)
    {
        Errors.SESSION_EXPIRED.event = runnable;
    }

    public void setNotAuthorizedEvent(Runnable runnable)
    {
        Errors.NOT_AUTHORIZED.event = runnable;
    }

    public Errors get()
    {
        return error;
    }

    public boolean ok()
    {
        return error == Errors.OK;
    }

    public boolean notOk()
    {
        return !ok();
    }

    public boolean error()
    {
        return error == Errors.ERROR;
    }

    public String msg()
    {
        return msg;
    }

    public void clear()
    {
        error = null;
        msg = "";
    }

    public String check(String message)
    {
        error = byCode(message);
        error.executeEvent();

        return msg = message.replaceFirst(Pattern.quote(error.code), "");
    }
}