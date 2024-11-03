package com.xorker.draw.mafia

import com.xorker.draw.BaseJpaEntity
import com.xorker.draw.player.PlayerHistoryJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType

@Entity
@Table(name = "mafia_game_result")
internal class MafiaGameResultJpaEntity : BaseJpaEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mafia_game_result_id")
    var id: Long = 0

    @Column(name = "game_result", columnDefinition = "TEXT")
    lateinit var gameResult: String
        protected set

    @Column(name = "language", columnDefinition = "varchar(20)")
    lateinit var language: String
        protected set

    @Column(name = "draw", columnDefinition = "TEXT")
    lateinit var draw: String
        protected set

    @Column(name = "mafia_answer", columnDefinition = "varchar(50)")
    var mafiaAnswer: String? = null
        protected set

    @Column(name = "is_random_matching", columnDefinition = "boolean")
    var isRandomMatching: Boolean = false
        protected set

    @Column(name = "word_id", columnDefinition = "bigint")
    var wordId: Long = 0
        protected set

    @OneToMany(mappedBy = "mafiaGameResult", fetch = FetchType.LAZY)
    @Cascade(CascadeType.PERSIST)
    val players: MutableList<PlayerHistoryJpaEntity> = mutableListOf()

    fun removePlayer(player: PlayerHistoryJpaEntity) {
        players.remove(player)
    }

    fun addPlayer(player: PlayerHistoryJpaEntity) {
        players.add(player)
    }

    companion object {
        internal fun of(locale: String, draw: String, mafiaAnswer: String?, isRandomMatching: Boolean, wordId: Long): MafiaGameResultJpaEntity {
            return MafiaGameResultJpaEntity().apply {
                this.language = locale
                this.draw = draw
                this.mafiaAnswer = mafiaAnswer
                this.isRandomMatching = isRandomMatching
                this.wordId = wordId
            }
        }
    }
}
