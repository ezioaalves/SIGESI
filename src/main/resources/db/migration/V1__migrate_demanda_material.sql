-- Migration: Convert demanda_material from join table to entity table
-- Run this BEFORE starting the application after the DemandaMaterial entity change.
--
-- The old demanda_material table was a pure join table (demanda_id + material_id composite PK).
-- The new DemandaMaterial entity requires: id (serial PK), demanda_id, material_id, quantidade.
--
-- This script drops the old table so Hibernate ddl-auto=update can recreate it properly.
-- WARNING: This will delete all existing demanda-material associations.

DROP TABLE IF EXISTS demanda_material CASCADE;
