package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import static edu.sou.cs452.jlox.generated.types.AbstractValue.*;

import java.util.HashMap;;

public class AbstractMap {

    public final static AbstractValue plus(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap < AbstractValue, HashMap < AbstractValue, AbstractValue >> lookup = new HashMap < > ();

        HashMap < AbstractValue, AbstractValue > left;

        left = new HashMap < > ();
        left.put(POSITIVE, POSITIVE);
        left.put(NEGATIVE, NEGATIVE);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(ZERO, left);

        left = new HashMap < > ();
        left.put(POSITIVE, BOTTOM);
        left.put(NEGATIVE, BOTTOM);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(BOTTOM, left);

        left = new HashMap < > ();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, TOP);
        left.put(BOTTOM, TOP);
        left.put(TOP, TOP);
        lookup.put(TOP, left);

        left = new HashMap < > ();
        left.put(POSITIVE, POSITIVE);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, POSITIVE);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(POSITIVE, left);

        left = new HashMap < > ();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, NEGATIVE);
        left.put(ZERO, NEGATIVE);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(NEGATIVE, left);

        return lookup.get(leftValue).get(rightValue);
    }

    public final static AbstractValue minus(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap < AbstractValue, HashMap < AbstractValue, AbstractValue >> lookup = new HashMap < > ();

        HashMap < AbstractValue, AbstractValue > left;

        left = new HashMap < > ();
        left.put(POSITIVE, NEGATIVE);
        left.put(NEGATIVE, POSITIVE);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(ZERO, left);

        left = new HashMap < > ();
        left.put(POSITIVE, BOTTOM);
        left.put(NEGATIVE, BOTTOM);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(BOTTOM, left);

        left = new HashMap < > ();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, TOP);
        left.put(BOTTOM, TOP);
        left.put(TOP, TOP);
        lookup.put(TOP, left);

        left = new HashMap < > ();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, POSITIVE);
        left.put(ZERO, POSITIVE);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(POSITIVE, left);

        left = new HashMap < > ();
        left.put(POSITIVE, NEGATIVE);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, NEGATIVE);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(NEGATIVE, left);

        return lookup.get(leftValue).get(rightValue);
    }

    public final static AbstractValue multiply(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap < AbstractValue, HashMap < AbstractValue, AbstractValue >> lookup = new HashMap < > ();

        HashMap < AbstractValue, AbstractValue > left;

        left = new HashMap < > ();
        left.put(POSITIVE, ZERO);
        left.put(NEGATIVE, ZERO);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, ZERO);
        left.put(TOP, ZERO);
        lookup.put(ZERO, left);

        left = new HashMap < > ();
        left.put(POSITIVE, BOTTOM);
        left.put(NEGATIVE, BOTTOM);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(BOTTOM, left);

        left = new HashMap < > ();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, TOP);
        left.put(TOP, TOP);
        lookup.put(TOP, left);

        left = new HashMap < > ();
        left.put(POSITIVE, POSITIVE);
        left.put(NEGATIVE, NEGATIVE);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(POSITIVE, left);

        left = new HashMap < > ();
        left.put(POSITIVE, NEGATIVE);
        left.put(NEGATIVE, POSITIVE);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(NEGATIVE, left);

        return lookup.get(leftValue).get(rightValue);
    }

    public final static AbstractValue divide(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap < AbstractValue, HashMap < AbstractValue, AbstractValue >> lookup = new HashMap < > ();

        HashMap < AbstractValue, AbstractValue > left;

        left = new HashMap < > ();
        left.put(POSITIVE, ZERO);
        left.put(NEGATIVE, ZERO);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, ZERO);
        lookup.put(ZERO, left);

        left = new HashMap < > ();
        left.put(POSITIVE, BOTTOM);
        left.put(NEGATIVE, BOTTOM);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, BOTTOM);
        lookup.put(BOTTOM, left);

        left = new HashMap < > ();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(TOP, left);

        left = new HashMap < > ();
        left.put(POSITIVE, POSITIVE);
        left.put(NEGATIVE, NEGATIVE);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(POSITIVE, left);

        left = new HashMap < > ();
        left.put(POSITIVE, NEGATIVE);
        left.put(NEGATIVE, POSITIVE);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(NEGATIVE, left);

        return lookup.get(leftValue).get(rightValue);
    }

    public final static AbstractValue invert(AbstractValue rightValue) {
        HashMap < AbstractValue, AbstractValue > lookup = new HashMap < > ();

        lookup.put(POSITIVE, NEGATIVE);
        lookup.put(NEGATIVE, POSITIVE);
        lookup.put(ZERO, ZERO);
        lookup.put(BOTTOM, BOTTOM);
        lookup.put(TOP, TOP);

        return lookup.get(rightValue);
    }

    public final static AbstractValue join(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap < AbstractValue, HashMap < AbstractValue, AbstractValue >> lookup = new HashMap < > ();

        HashMap < AbstractValue, AbstractValue > left;

        left = new HashMap < > ();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, ZERO);
        left.put(TOP, TOP);
        lookup.put(ZERO, left);

        left = new HashMap < > ();
        left.put(POSITIVE, POSITIVE);
        left.put(NEGATIVE, NEGATIVE);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(BOTTOM, left);

        left = new HashMap < > ();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, TOP);
        left.put(BOTTOM, TOP);
        left.put(TOP, TOP);
        lookup.put(TOP, left);

        left = new HashMap < > ();
        left.put(POSITIVE, POSITIVE);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, TOP);
        left.put(BOTTOM, POSITIVE);
        left.put(TOP, TOP);
        lookup.put(POSITIVE, left);

        left = new HashMap < > ();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, NEGATIVE);
        left.put(ZERO, TOP);
        left.put(BOTTOM, NEGATIVE);
        left.put(TOP, TOP);
        lookup.put(NEGATIVE, left);

        return lookup.get(leftValue).get(rightValue);
    }

}