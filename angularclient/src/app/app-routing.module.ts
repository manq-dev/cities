import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CityListComponent} from './component/city-list/city-list.component';

const routes: Routes = [
  { path: 'cities', component: CityListComponent },
  { path: '', redirectTo: 'cities', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
