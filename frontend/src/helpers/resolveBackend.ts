import { HttpConnection, Location, LocationService, Prefix } from '@tmtsoftware/esw-ts'

const backendServicePrefix = Prefix.fromString('ESW.library')
const connection = HttpConnection(backendServicePrefix, 'Service')

export const resolveBackendUrl = (locationService: LocationService): Promise<Location | undefined> =>
  locationService.find(connection).then(loc => loc)
