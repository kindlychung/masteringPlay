import filters.HeadersFilter
import play.api.GlobalSettings
import play.api.mvc.EssentialAction

object Global extends GlobalSettings {
  override def doFilter(action: EssentialAction): EssentialAction = HeadersFilter.noCache(action)
}