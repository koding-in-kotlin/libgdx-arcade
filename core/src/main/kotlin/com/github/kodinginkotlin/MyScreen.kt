import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.kodinginkotlin.FirstScreen
import com.github.kodinginkotlin.SecondScreen
import ktx.actors.onChange
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.scene2d.actors
import ktx.scene2d.table
import ktx.scene2d.textButton
import kotlin.system.exitProcess

class MyScreen(private val stage: Stage, val game: KtxGame<KtxScreen>) : KtxScreen {

    init {

        stage.actors {
            table {
                setFillParent(true)
                defaults().pad(10f)
                textButton("Start") {
                    onChange { processStart() }
                }
                row()
                textButton("Exit") {
                    onChange { processExit() }
                }
            }
        }
    }

    private fun processStart() {
        // your logic here
        println("Start")
        game.setScreen<FirstScreen>()
    }

    private fun processExit() {
        // your logic here
        println("Exit")
        exitProcess(0)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

}
