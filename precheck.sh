#!/bin/bash

sbt clean scalafmtSbt scalafmt Test/scalafmt it/scalafmt coverage test it/test scalafmtCheckAll coverageReport
