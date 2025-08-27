import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isAuthenticated()) {
    return true;
  }
  const tree: UrlTree = router.createUrlTree(['/auth/login'], {
    queryParams: { returnUrl: router.routerState.snapshot.url || '/' }
  });
  return tree;
};
