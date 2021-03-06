/*
 * Copyright 2016-2018 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kazari.domhelper

import org.scalajs.dom
import org.scalajs.dom._
import org.querki.jquery._
import org.scalaexercises.evaluator.Dependency

object DOMHelper extends DOMTags {
  val codeExcludeClass          = "kazari-exclude"
  val snippetsWithId            = "kazari-id-"
  val singleSnippets            = "kazari-single"
  val codeSnippetsSelectorById  = codeBlocksInDivsWithClass(snippetsWithId)
  val codeSingleSnippetSelector = codeBlocksInDivsWithClass(singleSnippets)
  val dependenciesMetaName      = "kazari-dependencies"
  val resolversMetaName         = "kazari-resolvers"

  def codeBlocksInDivsWithClass(className: String) = s"div[class*='$className'] code"

  def getMetaContent(metaTagName: String): String =
    Option($(s"meta[name=$metaTagName]").attr("content").get).getOrElse("")

  def classesFromElement(node: dom.Element): Seq[String] =
    node.attributes.getNamedItem("class").textContent.split(" ").toSeq

  def addClickListenerToButton(selector: String, function: (dom.MouseEvent) => Any) =
    Option(document.querySelector(selector)) foreach { b =>
      b.addEventListener("click", function)
    }

  def changeButtonIcon(selector: String, currentClass: String, nextClass: String) =
    Option(document.querySelector(selector)) foreach {
      $(_).removeClass(currentClass).addClass(nextClass)
    }

  def toggleButtonActiveState(selector: String, active: Boolean) =
    Option(document.querySelector(selector)).foreach { b =>
      val _ = if (active) {
        $(b).addClass(decoratorButtonDisableClass)
      } else {
        $(b).removeClass(decoratorButtonDisableClass)
      }
    }

  def showAlertMessage(parentSelector: String, message: String, isSuccess: Boolean) =
    Option(document.querySelector(s"$parentSelector .$decoratorAlertBarClass")) foreach { a =>
      val classToApply =
        if (isSuccess) { decoratorAlertBarSuccessClass } else { decoratorAlertBarErrorClass }
      $(a).removeClass(decoratorAlertBarHiddenClass).addClass(classToApply).text(message)
    }

  def hideAlertMessage(parentSelector: String) =
    Option(document.querySelector(s"$parentSelector .$decoratorAlertBarClass")) foreach { a =>
      $(a)
        .removeClass(decoratorAlertBarSuccessClass)
        .removeClass(decoratorAlertBarErrorClass)
        .addClass(decoratorAlertBarHiddenClass)
        .text("")
    }

  def decorationSnippetId(index: Int): String = s"snippet-$index"

  def snippetIndexFromDecorationId(decorationId: String): Option[Int] = {
    def parseInt(src: String): Option[Int] =
      try {
        Some(src.toInt)
      } catch {
        case e: Throwable => None
      }
    parseInt(decorationId.split("-").toSeq.last)
  }

  def getHeightFromElement(selector: String): Option[Double] =
    Option($($(selector)).height())

  def closestParentDiv(node: dom.Node): JQuery = $(node).parents("div").first()

  def getDependenciesList(): List[Dependency] = {
    val content  = getMetaContent(dependenciesMetaName)
    val elements = content.split(",")

    elements
      .foldRight(Seq[Dependency]()) {
        case (e, l) =>
          val split = e.split(";")
          if (split.length == 3) {
            l ++ Seq(Dependency(split(0), split(1), split(2)))
          } else {
            l
          }
      }
      .toList
  }

  def getResolversList(): List[String] = {
    val content = getMetaContent(resolversMetaName)
    if (content == "") {
      List()
    } else {
      content.split(",").toList
    }
  }

}
