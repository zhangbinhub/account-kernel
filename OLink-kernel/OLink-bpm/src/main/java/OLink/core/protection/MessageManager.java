package OLink.core.protection;

import OLink.bpm.util.property.PropertyUtil;

public class MessageManager
{
    private IValidator validator;
    private static MessageManager ins = null;

    private MessageManager(IValidator validator)
    {
        this.validator = validator;
    }

    public static MessageManager getInstance(IValidator validator) {
        if ((ins == null) || (ins.validator != validator))
            ins = new MessageManager(validator);
        return ins;
    }

    public static MessageManager getInstance()
    {
        if (ins == null)
            ins = new MessageManager(new Validator(null, null));
        return ins;
    }

    public IReceiver getReceiver() throws Exception {
        if (validate())
        {
            return new ShortReceiver();
        }
        return null;
    }

    private boolean validate() throws Exception {
        return this.validator.validate();
    }

    public ISender getSender() throws Exception {
        if (validate())
        {
            return new ShortSender();
        }
        return null;
    }

    public IValidator getValidator() {
        return this.validator;
    }

    private Object getObject(String key)
    {
        Object object = null;
        try {
            String className = PropertyUtil.getByPropName("shortmessage", key);
            object = Class.forName(className).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }
}