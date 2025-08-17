import 'package:flutter/cupertino.dart';

class ForcedValueNotifier<T> extends ValueNotifier<T> {
  ForcedValueNotifier(super.value);

  @override
  set value(T newValue) {
    /*
    The below code is extracted from the original 'ValueNotifier'. The only
    difference the 'ForcedValueNotifier' has, is that it notifies all listeners
    no matter if the new value is equivalent to the original value or not.

    if (_value == newValue) {
      return;
    }
    */

    super.notifyListeners();
    super.value = newValue;
  }
}