# Changelog

All notable changes to this project will be documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).
This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/).

## [0.2.0] - 2026-03-17

### Documentation

- Update for v0.1.0 [skip ci] ([`135304a`](https://github.com/dweidle/copilot-agentic-playground/commit/135304a4fde61fc2d186fc961fcb37c366cc3f16))



### Features

- Add random multilingual greeting with flag display (#14) ([`7d156f2`](https://github.com/dweidle/copilot-agentic-playground/commit/7d156f2277c08c028c4ea3e7981bb97b5a1f63a3))



### Maintenance

- Trigger automatically on main push with auto minor version bump ([`b48fa68`](https://github.com/dweidle/copilot-agentic-playground/commit/b48fa68c59320f24a9bd1a95b344337ca2e73bf3))


- Verify auto-release trigger on main push ([`a0a2f6d`](https://github.com/dweidle/copilot-agentic-playground/commit/a0a2f6d8e4032eabdeea0b2806c3967279a085f9))



## [0.1.0] - 2026-03-16

### Bug Fixes

- Add testcontainers-bom for spring-boot 4.0 compatibility ([`8534fbd`](https://github.com/dweidle/copilot-agentic-playground/commit/8534fbd98b5f21f281ed9c7ec03801d5b4a91f05))


- Update test imports for spring-boot 4.0 package relocations ([`4423668`](https://github.com/dweidle/copilot-agentic-playground/commit/44236681eafa9a834c149877e56b4e7f7495069d))


- Add spring-boot-starter-webmvc-test for spring-boot 4.0 compatibility ([`d1e33ed`](https://github.com/dweidle/copilot-agentic-playground/commit/d1e33ed548f9a6df0d5cd2c03f9f3b208b358cd4))


- Align otel to 2.26.0 and fix TestRestTemplate registration for spring-boot 4.0 ([`31ec563`](https://github.com/dweidle/copilot-agentic-playground/commit/31ec563a9639b822d11bedd07fb74f456ab91161))


- Bump springdoc-openapi to 3.0.2 ([`cf35934`](https://github.com/dweidle/copilot-agentic-playground/commit/cf3593487e819805bde3cfb1f5d10c9a5d0fee1f))


- Bump cucumber to 7.34.3 and pin junit-platform 1.14.x ([`755776c`](https://github.com/dweidle/copilot-agentic-playground/commit/755776c9c37bc29adb5d588e7cb00844fcf941ad))


- Locate git-cliff binary via find after tar extraction ([`7717d3d`](https://github.com/dweidle/copilot-agentic-playground/commit/7717d3d87440d21f1602f81ab3d7e28f4cee0dc4))


- Add lmodern package for PDF generation ([`527e66c`](https://github.com/dweidle/copilot-agentic-playground/commit/527e66ce38dfb84f84730d533a09213860b62a9e))


- Remove PDF generation, release notes from CHANGELOG.md are sufficient ([`3f90b86`](https://github.com/dweidle/copilot-agentic-playground/commit/3f90b869519471f71f81a3211654b306f9dfe695))



### Documentation

- Add arc42 architecture documentation ([`40ced1e`](https://github.com/dweidle/copilot-agentic-playground/commit/40ced1eff33aa5ccc06d42a4523055184732b7ee))


- Add copilot instructions ([`8d70e81`](https://github.com/dweidle/copilot-agentic-playground/commit/8d70e81b31b75468e75b1555c9eb40bc781e49e5))


- Mark all arc42 risks as mitigated ([`00484cf`](https://github.com/dweidle/copilot-agentic-playground/commit/00484cf895d5ff2f95ea3d7c07f1c4e4302d8937))


- Update architecture docs for hexagonal structure ([`3b85c46`](https://github.com/dweidle/copilot-agentic-playground/commit/3b85c4679af75a64c26fde4c3805df2c03b597e6))


- Add PR health check procedure including merge conflict detection ([`f5489e1`](https://github.com/dweidle/copilot-agentic-playground/commit/f5489e1efaa17c75761e33265719429db7b1a519))


- Add project README ([`227fb57`](https://github.com/dweidle/copilot-agentic-playground/commit/227fb5764488c4e2e4bb2c66e6004eb003ff1e71))


- Establish C4 PlantUML as diagram standard ([`d0b4c6c`](https://github.com/dweidle/copilot-agentic-playground/commit/d0b4c6cfa48cf65c4f20143ab8c7ef728975ebdb))


- Add C4 PlantUML architecture diagrams ([`7ccc4cc`](https://github.com/dweidle/copilot-agentic-playground/commit/7ccc4cc30c9f10f6bd1170da2b8554775f35151e))


- Update for v0.1.0 [skip ci] ([`ce0231c`](https://github.com/dweidle/copilot-agentic-playground/commit/ce0231cf5e63b49a9c5de718751fa1085a1b2b3a))


- Update for v0.1.0 [skip ci] ([`fd96a85`](https://github.com/dweidle/copilot-agentic-playground/commit/fd96a854b3b3a5bfc8a1023d8b672342c510f411))



### Features

- Initialize spring boot application ([`28b1bed`](https://github.com/dweidle/copilot-agentic-playground/commit/28b1bed9a7602cfe408f38614615f3d3430cfed0))


- Add greeting REST endpoint ([`996ab64`](https://github.com/dweidle/copilot-agentic-playground/commit/996ab64a96737b510e00157d577484db06cc51cf))


- Persist greeting requests to postgresql ([`3a293c3`](https://github.com/dweidle/copilot-agentic-playground/commit/3a293c39120cc1f2eab4917966ccd5bcb6bfa1f2))


- Add global error handling with @ControllerAdvice ([`fec18a2`](https://github.com/dweidle/copilot-agentic-playground/commit/fec18a21b4a766a8a6db2c3c3272071d0299c463))


- Add react frontend for greeting service ([`8c7c1a1`](https://github.com/dweidle/copilot-agentic-playground/commit/8c7c1a1c1389b8a0be6e0c365b8824e066222e64))


- Add openapi/swagger documentation ([`9ad6836`](https://github.com/dweidle/copilot-agentic-playground/commit/9ad68363dbc023f7a245b8402b6becdb45ccafcb))


- Introduce persistence feature flag via @ConditionalOnProperty ([`b143da2`](https://github.com/dweidle/copilot-agentic-playground/commit/b143da2a0db8ec61ce7dcc7cfa315b702af1c38f))


- Add OpenTelemetry application logging ([`8c9bbd3`](https://github.com/dweidle/copilot-agentic-playground/commit/8c9bbd3500c768e039c12c3dc5cb4cd88ac72537))


- Add grafana otel-lgtm stack to docker compose ([`a92442c`](https://github.com/dweidle/copilot-agentic-playground/commit/a92442c9c6c0ddf76842508592d4c1e3d9fce0b2))


- Add spring profiles and actuator ([`0019570`](https://github.com/dweidle/copilot-agentic-playground/commit/0019570b20dd7d8daed565f703e984a580f7c4c3))


- Automate release note generation with Markdown and PDF output ([`866edb5`](https://github.com/dweidle/copilot-agentic-playground/commit/866edb529ebde457eff6e9eb3529d9c765c2a2b4))


- Add release-engineer agent for git/PR/versioning workflow ([`8bff006`](https://github.com/dweidle/copilot-agentic-playground/commit/8bff006ac90440759f5c4d33b0f04e91b1db51fb))


- Add wave emoji and shake animation (#11) ([`a61dd01`](https://github.com/dweidle/copilot-agentic-playground/commit/a61dd01155157f76485be04c6586fbeab46237c4))


- Add Flyway migration for greeting_log table ([`b48074e`](https://github.com/dweidle/copilot-agentic-playground/commit/b48074ee2f66000363bd297a2c688b616de07515))


- Add Flyway dependencies for database migrations ([`9c5ac9d`](https://github.com/dweidle/copilot-agentic-playground/commit/9c5ac9d382c727e1c12ef14e952e86956f5bb208))



### Maintenance

- Add ci/cd pipeline and dependabot ([`42cdf29`](https://github.com/dweidle/copilot-agentic-playground/commit/42cdf29135e1ed9296b5614151d82d4bda61d91c))


- Expose trace correlation IDs in console log appender ([`ede7eec`](https://github.com/dweidle/copilot-agentic-playground/commit/ede7eec3d5f27adab78d617cd8862e3d642cacfc))


- Bump com.diffplug.spotless:spotless-maven-plugin ([`bc098c1`](https://github.com/dweidle/copilot-agentic-playground/commit/bc098c1b83af5f1197f578276e5c3300429eedc0))


- Add issue-to-PR workflow and helper script ([`aa3fe71`](https://github.com/dweidle/copilot-agentic-playground/commit/aa3fe7110ad9ac96bc8f846376ae3564e774ab22))


- Bump actions/setup-java from 4 to 5 ([`6ddbc6b`](https://github.com/dweidle/copilot-agentic-playground/commit/6ddbc6ba9bba7826bde34895e1246faaf9f6eb56))


- Bump actions/checkout from 4 to 6 ([`698eafa`](https://github.com/dweidle/copilot-agentic-playground/commit/698eafa006d9a39c1963fd5358281876dee2f139))


- Bump org.springframework.boot:spring-boot-starter-parent ([`9f3d9ef`](https://github.com/dweidle/copilot-agentic-playground/commit/9f3d9ef83d90d10b38dedd17ad6d97c32549ade7))


- Add git-cliff changelog automation ([`af7d44c`](https://github.com/dweidle/copilot-agentic-playground/commit/af7d44cc147efa013309b3b48318fd05e8a3700b))


- Fix model IDs to use dot notation ([`2d40ee3`](https://github.com/dweidle/copilot-agentic-playground/commit/2d40ee36ed3eb55fa19d454b42f0450ef0935294))



### Refactoring

- Restructure to hexagonal architecture ([`ce2e23e`](https://github.com/dweidle/copilot-agentic-playground/commit/ce2e23ec06a271f668039114c3834520d7164af4))


- Introduce lombok, mapstruct, and java records ([`b7b661c`](https://github.com/dweidle/copilot-agentic-playground/commit/b7b661c14a5fad56105f77e7d43e24cfbd83366f))


- Use spring RFC 7807 ProblemDetail for error responses ([`5e7223c`](https://github.com/dweidle/copilot-agentic-playground/commit/5e7223ca33e9cd810e518e265a34cc8339955c81))



### Tests

- Add controller and context load tests ([`1faf3e8`](https://github.com/dweidle/copilot-agentic-playground/commit/1faf3e852eb6592b4885b4ea3e2c78618f40750d))


- Add cucumber e2e tests ([`fc56b6d`](https://github.com/dweidle/copilot-agentic-playground/commit/fc56b6d59ca1dfd34b06abdd28cac899abcc84c6))


- Add vitest + testing library tests for GreetingForm ([`5c8bf00`](https://github.com/dweidle/copilot-agentic-playground/commit/5c8bf006eb0c5df5be05398ef274f5b3d575b517))




