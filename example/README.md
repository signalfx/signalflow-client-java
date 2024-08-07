# Example

This example runs a primitive `data('cpu.utilization').publish()` SignalFlow query and outputs every value from that
metric as it becomes available, starting from values from a minute ago until a minute from time of launching the
example. A token with API scope must be provided via `SPLUNK_ACCESS_TOKEN`, either as a system property or environment
variable. If the realm is not `us0`, the correct realm must be provided with `SPLUNK_REALM`.