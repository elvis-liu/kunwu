package com.thoughtworks.kunwu.dean;

import com.thoughtworks.kunwu.context.DeanContext;

import static com.thoughtworks.kunwu.dean.DeanDefinition.getDeanDefaultName;
import static com.thoughtworks.kunwu.utils.RuntimeAssert.fail;

public class DeanReferenceResolver {
    private final DeanContext deanContext;

    public DeanReferenceResolver(DeanContext deanContext) {
        this.deanContext = deanContext;
    }

    public Object[] getAllRefObjects(DeanReference[] paramRefs) {
        if (paramRefs == null || paramRefs.length == 0) {
            return new Object[0];
        }

        Object[] parameters = new Object[paramRefs.length];
        for (int i = 0; i < paramRefs.length; i++) {
            parameters[i] = getRefObject(paramRefs[i]);
        }
        return parameters;
    }

    public Class<?> getRefClassType(DeanReference ref) {
        Class<?> classType;

        switch (ref.getRefType()) {
            case CLASS:
            case VALUE: {
                classType = ref.getClassType();
                break;
            }
            case ID: {
                Object dean = deanContext.getDeanInstance(ref.getId());
                classType = dean.getClass();
                break;
            }
            default: {
                fail("Unknown ref type: " + ref.getRefType());
                classType = null;
                break;
            }
        }

        return classType;
    }

    public Object getRefObject(DeanReference ref) {
        Object refObj;
        switch (ref.getRefType()) {
            case CLASS: {
                refObj = deanContext.getDeanInstance(getDeanDefaultName(ref.getClassType()));
                break;
            }
            case VALUE: {
                refObj = ref.getValue();
                break;
            }
            case ID: {
                refObj = deanContext.getDeanInstance(ref.getId());
                break;
            }
            default: {
                fail("Unknown ref type: " + ref.getRefType());
                refObj = null;
                break;
            }
        }

        return refObj;
    }
}
