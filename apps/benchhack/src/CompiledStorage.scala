object CompiledStorage {

  private val map = scala.collection.mutable.WeakHashMap[String, List[Any]]()

  def check(id: String, current: List[Any]): Boolean =
    if (map.get(id).isEmpty) {
      map.update(id, current)
      true
    }
    else {
      val constBuff = map(id)
      if ((constBuff.length != current.length) || (constBuff zip current exists { case (buf, cur) => buf != cur })) {
        map.update(id, current)
        true
      }
      else {
        false
      }
    }

}
