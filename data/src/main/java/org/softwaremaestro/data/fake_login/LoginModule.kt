package org.softwaremaestro.data.fake_login

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.softwaremaestro.data.common.module.NetworkModule
import org.softwaremaestro.data.fake_login.fake.FakeAccessTokenDao
import org.softwaremaestro.data.fake_login.fake.FakeAccessTokenIssuer
import org.softwaremaestro.data.fake_login.fake.FakeAutoLoginApi
import org.softwaremaestro.data.fake_login.fake.FakeAutoLoginInterceptor
import org.softwaremaestro.data.fake_login.fake.FakeAutoLoginServer
import org.softwaremaestro.data.fake_login.fake.FakeIssueAccessTokenApi
import org.softwaremaestro.data.fake_login.fake.FakeIssueAccessTokenServer
import org.softwaremaestro.data.fake_login.fake.FakeIssueRefreshTokenApi
import org.softwaremaestro.data.fake_login.fake.FakeIssueRefreshTokenServer
import org.softwaremaestro.data.fake_login.fake.FakeRefreshTokenDao
import org.softwaremaestro.data.fake_login.fake.FakeLoginTokenAuthenticator
import org.softwaremaestro.data.fake_login.fake.FakeRefreshTokenIssuer
import org.softwaremaestro.data.fake_login.fake.FakeTokenInjector
import org.softwaremaestro.data.fake_login.fake.FakeUserIdentifier
import org.softwaremaestro.data.fake_login.impl.AutoLoginRepositoryImpl
import org.softwaremaestro.data.fake_login.impl.FakeAccessTokenStorage
import org.softwaremaestro.data.fake_login.impl.FakeRefreshTokenStorage
import org.softwaremaestro.data.fake_login.legacy.AccessTokenIssuer
import org.softwaremaestro.data.fake_login.legacy.AccessTokenStorage
import org.softwaremaestro.data.fake_login.legacy.AutoLoginApi
import org.softwaremaestro.data.fake_login.legacy.AutoLoginInterceptor
import org.softwaremaestro.data.fake_login.legacy.AutoLoginServer
import org.softwaremaestro.data.fake_login.legacy.IssueAccessTokenApi
import org.softwaremaestro.data.fake_login.legacy.IssueAccessTokenServer
import org.softwaremaestro.data.fake_login.legacy.IssueRefreshTokenApi
import org.softwaremaestro.data.fake_login.legacy.IssueRefreshTokenServer
import org.softwaremaestro.data.fake_login.legacy.RefreshTokenStorage
import org.softwaremaestro.data.fake_login.legacy.LoginTokenAuthenticator
import org.softwaremaestro.data.fake_login.legacy.LoginTokenInjector
import org.softwaremaestro.data.fake_login.legacy.LoginTokenRepositoryImpl
import org.softwaremaestro.data.fake_login.legacy.RefreshTokenIssuer
import org.softwaremaestro.data.fake_login.legacy.UserIdentifier
import org.softwaremaestro.domain.fake_login.AccessTokenDao
import org.softwaremaestro.domain.fake_login.AutoLoginRepository
import org.softwaremaestro.domain.fake_login.LoginTokenRepository
import org.softwaremaestro.domain.fake_login.RefreshTokenDao
import org.softwaremaestro.domain.fake_login.usecase.FakeAutoLoginUseCase
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
object LoginModule {
    @Provides
    @Singleton
    fun provideFakeAutoLoginUseCase(autoLoginRepository: AutoLoginRepository): FakeAutoLoginUseCase {
        return FakeAutoLoginUseCase(autoLoginRepository)
    }


    @Provides
    @Singleton
    fun provideAutoLoginRepository(autoLoginApi: AutoLoginApi): AutoLoginRepository {
        return AutoLoginRepositoryImpl(autoLoginApi)
    }

    @Provides
    @Singleton
    fun provideAutoLoginApi(interceptor: AutoLoginInterceptor): AutoLoginApi {
        return FakeAutoLoginApi(interceptor)
    }

    @Provides
    @Singleton
    fun provideAutoLoginInterceptor(
        loginTokenInjector: LoginTokenInjector, server: AutoLoginServer
    ): AutoLoginInterceptor {
        return FakeAutoLoginInterceptor(loginTokenInjector, server)
    }

    @Provides
    @Singleton
    fun provideAutoLoginServer(): AutoLoginServer {
        return FakeAutoLoginServer()
    }

    @Provides
    @Singleton
    fun provideIssueAccessTokenApi(server: IssueAccessTokenServer): IssueAccessTokenApi {
        return FakeIssueAccessTokenApi(server)
    }

    @Provides
    @Singleton
    fun provideIssueRefreshTokenApi(server: IssueRefreshTokenServer): IssueRefreshTokenApi {
        return FakeIssueRefreshTokenApi(server)
    }

    @Provides
    @Singleton
    fun provideIssueAccessTokenServer(): IssueAccessTokenServer {
        return FakeIssueAccessTokenServer()
    }

    @Provides
    @Singleton
    fun provideIssueRefreshTokenServer(): IssueRefreshTokenServer {
        return FakeIssueRefreshTokenServer()
    }

    @Provides
    @Singleton
    fun provideLoginTokenInjector(
        accessTokenDao: AccessTokenDao,
        loginTokenRepository: LoginTokenRepository
    ): LoginTokenInjector {
        return FakeTokenInjector(accessTokenDao, loginTokenRepository)
    }

    @Provides
    @Singleton
    fun provideLoginTokenRepository(
        loginTokenAuthenticator: LoginTokenAuthenticator,
        accessTokenIssuer: AccessTokenIssuer,
        refreshTokenIssuer: RefreshTokenIssuer
    ): LoginTokenRepository {
        return LoginTokenRepositoryImpl(loginTokenAuthenticator, accessTokenIssuer, refreshTokenIssuer)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        accessTokenDao: AccessTokenDao,
        refreshTokenDao: RefreshTokenDao
    ): LoginTokenAuthenticator {
        return FakeLoginTokenAuthenticator(accessTokenDao, refreshTokenDao)
    }

    @Provides
    @Singleton
    fun provideAccessTokenDao(
        accessTokenStorage: AccessTokenStorage,
        userIdentifier: UserIdentifier,
    ): AccessTokenDao {
        return FakeAccessTokenDao(accessTokenStorage, userIdentifier)
    }

    @Provides
    @Singleton
    fun provideRefreshTokenDao(
        refreshTokenStorage: RefreshTokenStorage,
        userIdentifier: UserIdentifier,
    ): RefreshTokenDao {
        return FakeRefreshTokenDao(refreshTokenStorage, userIdentifier)
    }

    @Provides
    @Singleton
    fun provideAccessTokenStorage(): AccessTokenStorage {
        return FakeAccessTokenStorage()
    }

    @Provides
    @Singleton
    fun provideRefreshTokenStorage(): RefreshTokenStorage {
        return FakeRefreshTokenStorage()
    }

    @Provides
    @Singleton
    fun provideUserIdentifier(): UserIdentifier {
        return FakeUserIdentifier()
    }

    @Provides
    @Singleton
    fun provideAccessTokenIssuer(
        issueAccessTokenApi: IssueAccessTokenApi,
        accessTokenDao: FakeAccessTokenDao,
        refreshTokenDao: FakeRefreshTokenDao
    ): AccessTokenIssuer {
        return FakeAccessTokenIssuer(issueAccessTokenApi, accessTokenDao, refreshTokenDao)
    }

    @Provides
    @Singleton
    fun provideRefreshTokenIssuer(
        issueRefreshTokenApi: IssueRefreshTokenApi,
        accessTokenDao: FakeAccessTokenDao,
        refreshTokenDao: FakeRefreshTokenDao
    ): RefreshTokenIssuer {
        return FakeRefreshTokenIssuer(issueRefreshTokenApi, accessTokenDao, refreshTokenDao)
    }
}