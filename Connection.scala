package object connection {
  
  class Signal[T] private[connection] {
    private[connection] val slots = collection.mutable.Set[Slot[T]]()

    private[connection] val destructor = () => {
      slots.foreach(_.disconnect(this))
      slots.clear()
    }

    def apply(message: T) = slots.foreach(x => x(message))
  }

  class Slot[T] private[connection] (val handler: (T) => Unit) {
    private[connection] val signals = collection.mutable.Set[Signal[T]]()

    private[connection] val destructor = () => {
      signals.foreach(disconnect)
      signals.clear()
    }

    def connect(signal: Signal[T]) {
      signals += signal
      signal.slots += this
    }

    def disconnect(signal: Signal[T]) {
      signals -= signal
      signal.slots -= this
    }

    def apply(message: T) = handler(message)
  }

  trait Connectable {
    private[connection] val destructors = collection.mutable.Set[() => Unit]()

    def Signal[T](): Signal[T] = {
      val signal = new Signal[T]()
      destructors += signal.destructor
      return signal
    }

    def Slot[T](callback: (T) => Unit): Slot[T] = {
      val slot = new Slot(callback)
      destructors += slot.destructor
      return slot
    }

    def breakConnections() {
      destructors.foreach(_())
    }
  }
}