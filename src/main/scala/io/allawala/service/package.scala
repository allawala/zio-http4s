package io.allawala

import zio.Has

package object service {
  type Bitly = Has[Bitly.Service]
}
