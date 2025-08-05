package com.github.hummel.prokhor.factory

import com.github.hummel.prokhor.service.*
import com.github.hummel.prokhor.service.impl.*

@Suppress("unused", "RedundantSuppression")
object ServiceFactory {
	val loginService: LoginService by lazy { LoginServiceImpl() }
	val botService: BotService by lazy { BotServiceImpl() }
	val memberService: MemberService by lazy { MemberServiceImpl() }
	val managerService: ManagerService by lazy { ManagerServiceImpl() }
	val ownerService: OwnerService by lazy { OwnerServiceImpl() }
	val dataService: DataService by lazy { DataServiceImpl() }
	val accessService: AccessService by lazy { AccessServiceImpl() }
}