import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';
import { AppShellComponent } from './features/shell/app-shell.component';
import { HistoryDetailPageComponent } from './features/history/history-detail-page.component';
import { HistoryPageComponent } from './features/history/history-page.component';
import { LoginPageComponent } from './features/auth/login-page.component';
import { SignupPageComponent } from './features/auth/signup-page.component';
import { PlayPageComponent } from './features/play/play-page.component';
import { ProfilePageComponent } from './features/profile/profile-page.component';
import { RankingDetailPageComponent } from './features/ranking/ranking-detail-page.component';
import { RankingPageComponent } from './features/ranking/ranking-page.component';

export const routes: Routes = [
	{
		path: 'auth',
		canActivate: [guestGuard],
		children: [
			{ path: 'login', component: LoginPageComponent },
			{ path: 'signup', component: SignupPageComponent },
			{ path: '', pathMatch: 'full', redirectTo: 'login' }
		]
	},
	{
		path: 'app',
		component: AppShellComponent,
		canActivate: [authGuard],
		children: [
			{ path: 'play', component: PlayPageComponent },
			{ path: 'history', component: HistoryPageComponent },
			{ path: 'history/:uuid', component: HistoryDetailPageComponent },
			{ path: 'ranking', component: RankingPageComponent },
			{ path: 'ranking/:uuid', component: RankingDetailPageComponent },
			{ path: 'profile', component: ProfilePageComponent },
			{ path: '', pathMatch: 'full', redirectTo: 'play' }
		]
	},
	{ path: '', pathMatch: 'full', redirectTo: 'app/play' },
	{ path: '**', redirectTo: 'app/play' }
];
