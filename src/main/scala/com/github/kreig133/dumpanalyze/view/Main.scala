package com.github.kreig133.dumpanalyze.view

import java.util

import com.github.kreig133.dumpanalyze.parsing.Parser

import javafx.event.{ActionEvent, EventHandler}

import scala.io.Source
import scalafx.application.{Platform, JFXApp}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.TableColumn._
import scalafx.scene.control._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.stage.FileChooser


class ThreadModel(firstName_ : String,
             state_ : String,
             favoriteColor_ : Color) {
  val threadName = new StringProperty(this, "threadName", firstName_)
  val state = new StringProperty(this, "state", state_)
  val favoriteColor = new ObjectProperty(this, "favoriteColor", favoriteColor_)
}

object TableWithCustomCellDemo extends JFXApp {

  val characters = ObservableBuffer[ThreadModel](
    new ThreadModel("Peggy", "Sue", Color.Violet),
    new ThreadModel("Rocky", "Raccoon", Color.GreenYellow),
    new ThreadModel("Bungalow ", "Bill", Color.DarkSalmon)
  )

  stage = new PrimaryStage {
    title = "TableView with custom color cell"
    scene = new Scene {

      content = List(
        new MenuBar {
          useSystemMenuBar = true
          menus = List(
            new Menu("Scala") {
              items = List(
                new Menu("Author Info") {
                  items = List(
                    new MenuItem("Type Safe"),
                    new MenuItem("Martin Odersky")
                  )
                },
                new MenuItem("Open ThreadDump") {
                  onAction = new EventHandler[ActionEvent] {
                    override def handle(event: ActionEvent): Unit = {
                      val fileChooser = new FileChooser() {
                        title = "Select a ThreadDump file"
                      }

                      val song = fileChooser.showOpenDialog(scene.delegate.get().getWindow)
                      val threadDump = Parser.parse(Source.fromFile(song).getLines())
                      characters.clear()
                      val l = new util.ArrayList[ThreadModel]()
                      threadDump.threads.map(t => new ThreadModel(t.vid, t.state.toString, Color.AliceBlue)).foreach(l.add)
                      characters.addAll(l)
                    }
                  }
                }
              )
            })
        },
        new TableView[ThreadModel](characters) {
          columns ++= List(
            new TableColumn[ThreadModel, String] {
              text = "Thread"
              cellValueFactory = {
                _.value.threadName
              }
              prefWidth = 100
            },
            new TableColumn[ThreadModel, String]() {
              text = "State"
              cellValueFactory = {
                _.value.state
              }
              prefWidth = 100
            },
            new TableColumn[ThreadModel, Color] {
              text = "Favorite Color"
              cellValueFactory = {
                _.value.favoriteColor
              }
              // Render the property value when it changes,
              // including initial assignment
              cellFactory = { _ =>
                new TableCell[ThreadModel, Color] {
                  item.onChange { (_, _, newColor) =>
                    graphic = new Circle {
                      fill = newColor
                      radius = 8
                    }
                  }
                }
              }
              prefWidth = 100
            }
          )
        })
    }
  }
}