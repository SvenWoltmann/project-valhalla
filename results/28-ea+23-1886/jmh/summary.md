## Sum over one million elements (25 measurements each)

| Array | Order | Without preview | With preview | Speedup |
|---|---|--:|--:|--:|
| `int[]` | sequential | 221.1 ± 1.6 us/op | 220.5 ± 0.5 us/op | 1.0× |
| `int[]` | shuffled | 220.5 ± 0.6 us/op | 220.5 ± 0.6 us/op | 1.0× |
| `Integer[]` | sequential | 250.1 ± 1.1 us/op | 235.1 ± 0.8 us/op | 1.1× |
| `Integer[]` | shuffled | 644.6 ± 6.1 us/op | 233.9 ± 0.5 us/op | 2.8× |
| `LocalDate[]` (years) | sequential | 247.2 ± 0.7 us/op | 229.7 ± 0.8 us/op | 1.1× |
| `LocalDate[]` (years) | shuffled | 634.2 ± 4.1 us/op | 230.2 ± 0.8 us/op | 2.8× |

## Value record that is not flattened (with preview)

| Array | Order | Time |
|---|---|--:|
| `IdentityPoint[]` (identity record) | sequential | 246.6 ± 1.5 us/op |
| `IdentityPoint[]` (identity record) | shuffled | 630.7 ± 5.3 us/op |
| `ValuePoint[]` (value record) | sequential | 307.8 ± 0.8 us/op |
| `ValuePoint[]` (value record) | shuffled | 1035.6 ± 38.7 us/op |
| `WideIdentityPoint[]` (identity record, 24 bytes) | sequential | 268.6 ± 0.8 us/op |
| `WideIdentityPoint[]` (identity record, 24 bytes) | shuffled | 938.2 ± 18.5 us/op |
