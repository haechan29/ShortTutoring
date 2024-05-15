package org.softwaremaestro.data.fake_login

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.softwaremaestro.data.common.module.NetworkModule
import org.softwaremaestro.data.fake_login.fake.AccessTokenStorageRepositoryImpl
import org.softwaremaestro.data.fake_login.fake.FakeAutoLoginApi
import org.softwaremaestro.data.fake_login.fake.FakeAutoLoginInterceptor
import org.softwaremaestro.data.fake_login.fake.FakeAutoLoginServer
import org.softwaremaestro.data.fake_login.fake.FakeIssueAccessTokenApi
import org.softwaremaestro.data.fake_login.fake.IssueAccessTokenRepositoryImpl
import org.softwaremaestro.data.fake_login.fake.FakeIssueAccessTokenServer
import org.softwaremaestro.data.fake_login.fake.FakeIssueRefreshTokenApi
import org.softwaremaestro.data.fake_login.fake.IssueRefreshTokenRepositoryImpl
import org.softwaremaestro.data.fake_login.fake.FakeIssueRefreshTokenServer
import org.softwaremaestro.data.fake_login.fake.RefreshTokenStorageRepositoryImpl
import org.softwaremaestro.data.fake_login.fake.FakeLoginTokenAuthenticator
import org.softwaremaestro.data.fake_login.fake.FakeTokenInjector
import org.softwaremaestro.data.fake_login.fake.FakeUserIdentifier
import org.softwaremaestro.data.fake_login.impl.AutoLoginRepositoryImpl
import org.softwaremaestro.data.fake_login.impl.FakeAccessTokenStorage
import org.softwaremaestro.data.fake_login.impl.FakeRefreshTokenStorage
import org.softwaremaestro.data.fake_login.legacy.AccessTokenStorage
import org.softwaremaestro.data.fake_login.legacy.AutoLoginApi
import org.softwaremaestro.data.fake_login.legacy.AutoLoginInterceptor
import org.softwaremaestro.data.fake_login.legacy.AutoLoginServer
import org.softwaremaestro.data.fake_login.legacy.IssueAccessTokenApi
import org.softwaremaestro.data.fake_login.legacy.IssueAccessTokenRepository
import org.softwaremaestro.data.fake_login.legacy.IssueAccessTokenServer
import org.softwaremaestro.data.fake_login.legacy.IssueRefreshTokenApi
import org.softwaremaestro.data.fake_login.legacy.IssueRefreshTokenServer
import org.softwaremaestro.data.fake_login.legacy.IssueLoginTokenRepository
import org.softwaremaestro.data.fake_login.legacy.IssueRefreshTokenRepository
import org.softwaremaestro.data.fake_login.legacy.RefreshTokenStorage
import org.softwaremaestro.data.fake_login.legacy.LoginTokenAuthenticator
import org.softwaremaestro.data.fake_login.legacy.TokenInjector
import org.softwaremaestro.data.fake_login.legacy.UserIdentifier
import org.softwaremaestro.domain.fake_login.AccessTokenStorageRepository
import org.softwaremaestro.domain.fake_login.AutoLoginRepository
import org.softwaremaestro.domain.fake_login.LoginTokenStorageRepository
import org.softwaremaestro.domain.fake_login.RefreshTokenStorageRepository
import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.LoginRefreshToken
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
        tokenInjector: TokenInjector, server: AutoLoginServer
    ): AutoLoginInterceptor {
        return FakeAutoLoginInterceptor(tokenInjector, server)
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
    fun provideTokenInjector(
        authenticator: LoginTokenAuthenticator,
        accessTokenStorageRepository: AccessTokenStorageRepository,
        issueAccessTokenRepository: IssueAccessTokenRepository,
        issueRefreshTokenRepository: IssueRefreshTokenRepository,
    ): TokenInjector {
        return FakeTokenInjector(authenticator, accessTokenStorageRepository, issueAccessTokenRepository, issueRefreshTokenRepository)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        accessTokenStorageRepository: AccessTokenStorageRepository,
        refreshTokenStorageRepository: RefreshTokenStorageRepository
    ): LoginTokenAuthenticator {
        return FakeLoginTokenAuthenticator(accessTokenStorageRepository, refreshTokenStorageRepository)
    }

    @Provides
    @Singleton
    fun provideAccessTokenStorageRepository(
        accessTokenStorage: AccessTokenStorage,
        userIdentifier: UserIdentifier,
    ): AccessTokenStorageRepository {
        return AccessTokenStorageRepositoryImpl(accessTokenStorage, userIdentifier)
    }

    @Provides
    @Singleton
    fun provideRefreshTokenStorageRepository(
        refreshTokenStorage: RefreshTokenStorage,
        userIdentifier: UserIdentifier,
    ): RefreshTokenStorageRepository {
        return RefreshTokenStorageRepositoryImpl(refreshTokenStorage, userIdentifier)
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
    fun provideIssueAccessTokenRepository(
        issueAccessTokenApi: IssueAccessTokenApi,
        accessTokenStorageRepository: AccessTokenStorageRepositoryImpl,
        refreshTokenStorageRepository: RefreshTokenStorageRepositoryImpl
    ): IssueAccessTokenRepository {
        return IssueAccessTokenRepositoryImpl(issueAccessTokenApi, accessTokenStorageRepository, refreshTokenStorageRepository)
    }

    @Provides
    @Singleton
    fun provideIssueRefreshTokenRepository(
        issueRefreshTokenApi: IssueRefreshTokenApi,
        accessTokenStorageRepository: AccessTokenStorageRepositoryImpl,
        refreshTokenStorageRepository: RefreshTokenStorageRepositoryImpl
    ): IssueRefreshTokenRepository {
        return IssueRefreshTokenRepositoryImpl(issueRefreshTokenApi, accessTokenStorageRepository, refreshTokenStorageRepository)
    }
}