#!/bin/bash

mysqladmin ping -uroot --silent && exit 0 || exit 1