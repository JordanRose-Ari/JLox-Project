package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

class ReturnException extends RuntimeException {
  final LiteralValue value;

  ReturnException(LiteralValue value) {
    super(null, null, false, false);
    this.value = value;
  }
}