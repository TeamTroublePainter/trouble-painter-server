package com.xorker.draw.player

import com.xorker.draw.BaseJpaEntity
import com.xorker.draw.mafia.MafiaGameResultJpaEntity
import com.xorker.draw.user.UserJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "player")
internal class PlayerJpaEntity : BaseJpaEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id", nullable = false)
    var id: Long = 0
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "result", columnDefinition = "varchar(20)")
    lateinit var result: ResultType
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "varchar(20)")
    lateinit var role: RoleType
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    lateinit var user: UserJpaEntity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mafia_game_result_id")
    lateinit var mafiaGameResult: MafiaGameResultJpaEntity

    fun addMafiaGameResult(gameResult: MafiaGameResultJpaEntity) {
        mafiaGameResult = gameResult
        gameResult.addPlayer(this)
    }

    companion object {
        fun of(result: ResultType, role: RoleType, user: UserJpaEntity, gameResult: MafiaGameResultJpaEntity): PlayerJpaEntity {
            val player = PlayerJpaEntity().apply {
                this.result = result
                this.role = role
                this.user = user
            }
            player.addMafiaGameResult(gameResult)
            return player
        }
    }
}
