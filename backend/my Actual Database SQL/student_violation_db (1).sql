-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 01, 2025 at 05:33 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `student_violation_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `penalty_matrix`
--

CREATE TABLE `penalty_matrix` (
  `id` int(11) NOT NULL,
  `violation_type` varchar(255) NOT NULL,
  `offense_count` int(11) NOT NULL,
  `penalty_description` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `penalty_matrix`
--

INSERT INTO `penalty_matrix` (`id`, `violation_type`, `offense_count`, `penalty_description`) VALUES
(1, 'No ID', 1, 'Warning'),
(2, 'No ID', 2, 'Grounding'),
(3, 'No ID', 3, 'Suspension'),
(4, 'Wearing of rubber slippers', 1, 'Warning'),
(5, 'Wearing of rubber slippers', 2, 'Grounding'),
(6, 'Wearing of rubber slippers', 3, 'Suspension'),
(7, 'Improper wearing of uniform', 1, 'Warning'),
(8, 'Improper wearing of uniform', 2, 'Grounding'),
(9, 'Improper wearing of uniform', 3, 'Suspension'),
(10, 'Non-prescribed haircut', 1, 'Warning'),
(11, 'Non-prescribed haircut', 2, 'Grounding'),
(12, 'Non-prescribed haircut', 3, 'Suspension'),
(13, 'Wearing of earring', 1, 'Warning'),
(14, 'Wearing of earring', 2, 'Grounding'),
(15, 'Wearing of earring', 3, 'Suspension'),
(16, 'Wearing of earrings', 1, 'Warning'),
(17, 'Wearing of earrings', 2, 'Grounding'),
(18, 'Wearing of earrings', 3, 'Suspension'),
(19, 'Wearing of multiple earrings', 1, 'Warning'),
(20, 'Wearing of multiple earrings', 2, 'Grounding'),
(21, 'Wearing of multiple earrings', 3, 'Suspension'),
(22, 'Using cellphones/ gadgets during class hours', 1, 'Warning'),
(23, 'Using cellphones/ gadgets during class hours', 2, 'Grounding'),
(24, 'Using cellphones/ gadgets during class hours', 3, 'Probation'),
(25, 'Using cellphones/ gadgets during class hours.', 1, 'Warning'),
(26, 'Using cellphones/ gadgets during class hours.', 2, 'Grounding'),
(27, 'Using cellphones/ gadgets during class hours.', 3, 'Probation'),
(28, 'Eating inside the laboratories', 1, 'Warning'),
(29, 'Eating inside the laboratories', 2, 'Grounding'),
(30, 'Eating inside the laboratories', 3, 'Community service'),
(31, 'Improper not wearing/ tampering of ID', 1, 'Warning'),
(32, 'Improper not wearing/ tampering of ID', 2, 'Grounding'),
(33, 'Improper not wearing/ tampering of ID', 3, 'Probation'),
(34, 'Improper/tampered ID', 1, 'Warning'),
(35, 'Improper/tampered ID', 2, 'Grounding'),
(36, 'Improper/tampered ID', 3, 'Probation'),
(37, 'Improper hairstyle', 1, 'Warning'),
(38, 'Improper hairstyle', 2, 'Grounding'),
(39, 'Improper hairstyle', 3, 'Suspension'),
(40, 'Improper Uniform', 1, 'Warning'),
(41, 'Improper Uniform', 2, 'Grounding'),
(42, 'Improper Uniform', 3, 'Suspension'),
(43, 'Stealing', 1, 'Suspension'),
(44, 'Stealing', 2, 'Non-readmission'),
(45, 'Stealing', 3, 'Expulsion'),
(46, 'Vandalism', 1, 'Suspension'),
(47, 'Vandalism', 2, 'Community service + Probation'),
(48, 'Vandalism', 3, 'Expulsion'),
(49, 'Verbal assault', 1, 'Probation'),
(50, 'Verbal assault', 2, 'Suspension'),
(51, 'Verbal assault', 3, 'Non-readmission'),
(52, 'Organizing, planning or joining to any group or fraternity activity', 1, 'Suspension'),
(53, 'Organizing, planning or joining to any group or fraternity activity', 2, 'Non-readmission'),
(54, 'Organizing, planning or joining to any group or fraternity activity', 3, 'Expulsion'),
(55, 'Organizing, planning or joining to any group or fraternity activity.', 1, 'Suspension'),
(56, 'Organizing, planning or joining to any group or fraternity activity.', 2, 'Non-readmission'),
(57, 'Organizing, planning or joining to any group or fraternity activity.', 3, 'Expulsion'),
(58, 'Organizing/joining fraternity activities', 1, 'Suspension'),
(59, 'Organizing/joining fraternity activities', 2, 'Non-readmission'),
(60, 'Organizing/joining fraternity activities', 3, 'Expulsion'),
(61, 'Cutting Classes', 1, 'Warning'),
(62, 'Cutting Classes', 2, 'Grounding'),
(63, 'Cutting Classes', 3, 'Suspension'),
(64, 'Cheating/Academic Dishonesty', 1, 'Suspension'),
(65, 'Cheating/Academic Dishonesty', 2, 'Probation'),
(66, 'Cheating/Academic Dishonesty', 3, 'Expulsion'),
(67, 'Cheating / Academic Dishonesty', 1, 'Suspension'),
(68, 'Cheating / Academic Dishonesty', 2, 'Probation'),
(69, 'Cheating / Academic Dishonesty', 3, 'Expulsion'),
(70, 'Theft/Stealing', 1, 'Suspension'),
(71, 'Theft/Stealing', 2, 'Non-readmission'),
(72, 'Theft/Stealing', 3, 'Expulsion'),
(73, 'Theft / Stealing', 1, 'Suspension'),
(74, 'Theft / Stealing', 2, 'Non-readmission'),
(75, 'Theft / Stealing', 3, 'Expulsion'),
(76, 'Inflicting/Direct Assault', 1, 'Suspension'),
(77, 'Inflicting/Direct Assault', 2, 'Non-readmission'),
(78, 'Inflicting/Direct Assault', 3, 'Expulsion'),
(79, 'Inflicting / Direct Assault', 1, 'Suspension'),
(80, 'Inflicting / Direct Assault', 2, 'Non-readmission'),
(81, 'Inflicting / Direct Assault', 3, 'Expulsion'),
(82, 'Gambling', 1, 'Probation'),
(83, 'Gambling', 2, 'Suspension'),
(84, 'Gambling', 3, 'Expulsion'),
(85, 'Smoking within the school vicinity', 1, 'Grounding'),
(86, 'Smoking within the school vicinity', 2, 'Suspension'),
(87, 'Smoking within the school vicinity', 3, 'Expulsion'),
(88, 'Smoking within the school', 1, 'Grounding'),
(89, 'Smoking within the school', 2, 'Suspension'),
(90, 'Smoking within the school', 3, 'Expulsion'),
(91, 'Possession/Use of Prohibited Drugs', 1, 'Suspension'),
(92, 'Possession/Use of Prohibited Drugs', 2, 'Non-readmission'),
(93, 'Possession/Use of Prohibited Drugs', 3, 'Expulsion'),
(94, 'Use/Possession of Prohibited Drugs', 1, 'Suspension'),
(95, 'Use/Possession of Prohibited Drugs', 2, 'Non-readmission'),
(96, 'Use/Possession of Prohibited Drugs', 3, 'Expulsion'),
(97, 'Possession/Use of Liquor/Alcoholic Beverages', 1, 'Suspension'),
(98, 'Possession/Use of Liquor/Alcoholic Beverages', 2, 'Non-readmission'),
(99, 'Possession/Use of Liquor/Alcoholic Beverages', 3, 'Expulsion'),
(100, 'Use/Possession of Liquor/Alcohol', 1, 'Suspension'),
(101, 'Use/Possession of Liquor/Alcohol', 2, 'Non-readmission'),
(102, 'Use/Possession of Liquor/Alcohol', 3, 'Expulsion'),
(103, 'Others', 1, 'Warning/Probation'),
(104, 'Others', 2, 'Suspension'),
(105, 'Others', 3, 'Expulsion');

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `id` int(11) NOT NULL,
  `student_id` varchar(50) NOT NULL,
  `student_name` varchar(255) NOT NULL,
  `year_level` varchar(50) NOT NULL,
  `course` varchar(100) NOT NULL,
  `section` varchar(50) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `added_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`id`, `student_id`, `student_name`, `year_level`, `course`, `section`, `password`, `added_at`, `updated_at`) VALUES
(1, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', '$2y$10$DBvE6ujqSbiqOgYIFk1RaOMLSUQRlvAtyIQx2hNhxZkZgAwLIYyN.', '2025-07-22 06:57:35', '2025-07-23 12:43:43'),
(2, '123456', 'Joshua Pavia Basco', '4th Year', 'BSCS', 'BS8MA', '$2y$10$p20Z9rcqlk1a16suel4plu5sV8JdFzLVPg17WZEzs8n7Rj1sHJWAS', '2025-08-03 13:27:42', '2025-08-03 13:30:56'),
(3, '2021-0001', 'John Doe', '4th Year', 'BSIT', 'A', NULL, '2025-08-31 10:31:47', '2025-08-31 10:31:47'),
(4, '2021-0002', 'Jane Smith', '3rd Year', 'BSCS', 'B', NULL, '2025-08-31 10:31:47', '2025-08-31 10:31:47'),
(5, '2021-0003', 'Mike Johnson', '2nd Year', 'BSIT', 'A', NULL, '2025-08-31 10:31:47', '2025-08-31 10:31:47'),
(6, '2022-0001', 'Sarah Wilson', '3rd Year', 'BSCS', 'C', NULL, '2025-08-31 10:31:47', '2025-08-31 10:31:47'),
(7, '2022-0002', 'David Brown', '4th Year', 'BSIT', 'B', NULL, '2025-08-31 10:31:47', '2025-08-31 10:31:47');

-- --------------------------------------------------------

--
-- Stand-in structure for view `student_stats`
-- (See below for the actual view)
--
CREATE TABLE `student_stats` (
);

-- --------------------------------------------------------

--
-- Table structure for table `student_violation_offense_counts`
--

CREATE TABLE `student_violation_offense_counts` (
  `id` int(11) NOT NULL,
  `student_id` varchar(50) NOT NULL,
  `violation_type` varchar(255) NOT NULL,
  `offense_count` int(11) NOT NULL DEFAULT 0,
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student_violation_offense_counts`
--

INSERT INTO `student_violation_offense_counts` (`id`, `student_id`, `violation_type`, `offense_count`, `last_updated`) VALUES
(319, '220342', 'No ID', 3, '2025-08-31 17:42:49'),
(320, '220342', 'Wearing of rubber slippers', 2, '2025-08-31 17:15:56'),
(321, '220342', 'Improper wearing of uniform', 3, '2025-08-31 17:59:11'),
(322, '220342', 'Non-prescribed haircut', 1, '2025-08-31 17:15:35'),
(323, '220342', 'Wearing of earring', 1, '2025-08-31 17:15:35'),
(324, '220342', 'Wearing of multiple earrings', 1, '2025-08-31 17:15:35'),
(325, '220342', 'Cutting Classes', 1, '2025-08-31 17:15:35'),
(326, '220342', 'Cheating/Academic Dishonesty', 1, '2025-08-31 17:15:35'),
(327, '220342', 'Theft/Stealing', 1, '2025-08-31 17:15:35'),
(328, '220342', 'Inflicting/Direct Assault', 1, '2025-08-31 17:15:35'),
(329, '220342', 'Gambling', 1, '2025-08-31 17:15:35'),
(330, '220342', 'Smoking within the school vicinity', 1, '2025-08-31 17:15:35'),
(331, '220342', 'Possession/Use of Prohibited Drugs', 1, '2025-08-31 17:15:35'),
(332, '220342', 'Possession/Use of Liquor/Alcoholic Beverages', 1, '2025-08-31 17:15:35'),
(333, '220342', 'Others', 3, '2025-08-31 17:41:35'),
(334, '220342', 'Using cellphones/ gadgets during class hours', 1, '2025-08-31 17:15:35'),
(335, '220342', 'Eating inside the laboratories', 1, '2025-08-31 17:15:35'),
(336, '220342', 'Improper not wearing/ tampering of ID', 2, '2025-08-31 17:22:48'),
(337, '220342', 'Improper hairstyle', 2, '2025-08-31 17:22:48'),
(338, '220342', 'Improper Uniform', 2, '2025-08-31 17:22:48'),
(339, '220342', 'Stealing', 2, '2025-08-31 17:22:48'),
(340, '220342', 'Vandalism', 2, '2025-08-31 17:22:48'),
(341, '220342', 'Verbal assault', 2, '2025-08-31 17:22:48'),
(342, '220342', 'Organizing, planning or joining to any group or fraternity activity', 2, '2025-08-31 17:22:48');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','guard','teacher') DEFAULT 'guard',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `password`, `role`, `created_at`, `updated_at`) VALUES
(1, 'Admin', 'admin@aics.edu.ph', '$2y$10$zUNada4kWb9FGDNkphPwme3Af1F1sYtkcDGp4aNz7Pm/t8Z4Deb1q', 'guard', '2025-07-22 09:10:47', '2025-07-22 09:10:47'),
(2, 'Guard01', 'guard1@aics.edu.ph', '$2y$10$3aiIpD9pW8PeDAQf4eAW8.2D5cHyIulcovoS2Qj7YyawOZxra9XkK', 'guard', '2025-07-23 05:19:30', '2025-07-23 05:19:30'),
(3, 'guard1', 'guard@violationsapp.com', 'guard123', 'guard', '2025-08-31 10:31:47', '2025-08-31 10:31:47'),
(4, 'teacher1', 'teacher@violationsapp.com', 'teacher123', 'teacher', '2025-08-31 10:31:47', '2025-08-31 10:31:47'),
(5, 'Joshua', 'joshuapaviabasco@gmail.com', '12345678', 'guard', '2025-08-31 11:32:58', '2025-08-31 11:32:58'),
(6, 'Joshua Pogi', 'Joshuapogi@aics.edu.p', '12345678', 'guard', '2025-08-31 13:18:51', '2025-08-31 13:18:51'),
(7, 'ajJ', 'Joshua Pogi', '12345678', 'guard', '2025-08-31 13:45:20', '2025-08-31 13:45:20'),
(8, 'Guard', 'guard@aics.edu.ph', '12345678', 'guard', '2025-08-31 17:46:05', '2025-08-31 17:46:05');

-- --------------------------------------------------------

--
-- Table structure for table `violations`
--

CREATE TABLE `violations` (
  `id` int(11) NOT NULL,
  `student_id` varchar(50) NOT NULL,
  `student_name` varchar(255) NOT NULL,
  `year_level` varchar(50) NOT NULL,
  `course` varchar(100) NOT NULL,
  `section` varchar(50) NOT NULL,
  `offense_count` int(11) NOT NULL DEFAULT 1,
  `penalty` varchar(100) DEFAULT NULL,
  `recorded_by` varchar(255) NOT NULL,
  `recorded_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `acknowledged` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `violations`
--

INSERT INTO `violations` (`id`, `student_id`, `student_name`, `year_level`, `course`, `section`, `offense_count`, `penalty`, `recorded_by`, `recorded_at`, `acknowledged`) VALUES
(77, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 1, 'Warning', 'ajJ', '2025-08-31 16:09:29', 0),
(78, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 2, 'Grounding', 'ajJ', '2025-08-31 16:18:02', 0),
(79, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 3, 'Suspension', 'ajJ', '2025-08-31 16:18:15', 0),
(80, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 3, 'Suspension', 'ajJ', '2025-08-31 16:43:51', 0),
(81, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 3, 'Suspension', 'ajJ', '2025-08-31 17:05:09', 0),
(82, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 3, 'Suspension', 'ajJ', '2025-08-31 17:05:09', 0),
(83, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 3, 'Suspension', 'ajJ', '2025-08-31 17:05:41', 0),
(84, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 3, 'Expulsion', 'ajJ', '2025-08-31 17:06:18', 0),
(85, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 2, 'Non-readmission', 'ajJ', '2025-08-31 17:08:49', 0),
(86, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 3, 'Expulsion', 'ajJ', '2025-08-31 17:10:22', 0),
(87, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 1, 'Suspension', 'ajJ', '2025-08-31 17:10:45', 0),
(88, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 1, 'Suspension', 'ajJ', '2025-08-31 17:14:05', 0),
(89, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 2, 'Non-readmission', 'ajJ', '2025-08-31 17:14:46', 0),
(90, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 3, 'Expulsion', 'ajJ', '2025-08-31 17:14:54', 0),
(91, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 1, 'Warning', 'ajJ', '2025-08-31 17:15:35', 0),
(92, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 2, 'Grounding', 'ajJ', '2025-08-31 17:15:56', 0),
(93, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 2, 'Non-readmission', 'ajJ', '2025-08-31 17:22:48', 0),
(94, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 2, 'Suspension', 'ajJ', '2025-08-31 17:39:53', 0),
(95, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 3, 'Expulsion', 'ajJ', '2025-08-31 17:41:35', 0),
(96, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 3, 'Suspension', 'ajJ', '2025-08-31 17:42:49', 0),
(97, '220342', 'Joshua P. Basco', '4th Year', 'BSCS', 'BS7MA', 3, 'Suspension', 'Guard', '2025-08-31 17:59:11', 0);

-- --------------------------------------------------------

--
-- Table structure for table `violation_details`
--

CREATE TABLE `violation_details` (
  `id` int(11) NOT NULL,
  `violation_id` int(11) NOT NULL,
  `violation_type` varchar(255) NOT NULL,
  `violation_description` text DEFAULT NULL,
  `message_subject` varchar(255) DEFAULT NULL,
  `message_body` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `violation_details`
--

INSERT INTO `violation_details` (`id`, `violation_id`, `violation_type`, `violation_description`, `message_subject`, `message_body`) VALUES
(335, 77, 'No ID', NULL, NULL, NULL),
(336, 77, 'Wearing of rubber slippers', NULL, NULL, NULL),
(337, 77, 'Improper wearing of uniform', NULL, NULL, NULL),
(338, 78, 'No ID', NULL, NULL, NULL),
(339, 79, 'No ID', NULL, NULL, NULL),
(340, 79, 'Wearing of rubber slippers', NULL, NULL, NULL),
(341, 80, 'No ID', NULL, NULL, NULL),
(342, 80, 'Wearing of rubber slippers', NULL, NULL, NULL),
(343, 80, 'Improper wearing of uniform', NULL, NULL, NULL),
(344, 81, 'No ID', NULL, NULL, NULL),
(345, 81, 'Wearing of rubber slippers', NULL, NULL, NULL),
(346, 81, 'Improper wearing of uniform', NULL, NULL, NULL),
(347, 81, 'Non-prescribed haircut', NULL, NULL, NULL),
(348, 81, 'Wearing of earring', NULL, NULL, NULL),
(349, 81, 'Wearing of multiple earrings', NULL, NULL, NULL),
(350, 81, 'Improper Uniform', NULL, NULL, NULL),
(351, 81, 'Improper hairstyle', NULL, NULL, NULL),
(352, 81, 'Improper not wearing/ tampering of ID', NULL, NULL, NULL),
(353, 81, 'Eating inside the laboratories', NULL, NULL, NULL),
(354, 81, 'Using cellphones/ gadgets during class hours', NULL, NULL, NULL),
(355, 81, 'Others', NULL, NULL, NULL),
(356, 81, 'Possession/Use of Liquor/Alcoholic Beverages', NULL, NULL, NULL),
(357, 81, 'Possession/Use of Prohibited Drugs', NULL, NULL, NULL),
(358, 81, 'Smoking within the school vicinity', NULL, NULL, NULL),
(359, 81, 'Gambling', NULL, NULL, NULL),
(360, 81, 'Inflicting/Direct Assault', NULL, NULL, NULL),
(361, 81, 'Theft/Stealing', NULL, NULL, NULL),
(362, 81, 'Cheating/Academic Dishonesty', NULL, NULL, NULL),
(363, 81, 'Cutting Classes', NULL, NULL, NULL),
(364, 81, 'Organizing, planning or joining to any group or fraternity activity', NULL, NULL, NULL),
(365, 81, 'Verbal assault', NULL, NULL, NULL),
(366, 81, 'Vandalism', NULL, NULL, NULL),
(367, 81, 'Stealing', NULL, NULL, NULL),
(368, 82, 'No ID', NULL, NULL, NULL),
(369, 82, 'Wearing of rubber slippers', NULL, NULL, NULL),
(370, 82, 'Improper wearing of uniform', NULL, NULL, NULL),
(371, 82, 'Non-prescribed haircut', NULL, NULL, NULL),
(372, 82, 'Wearing of earring', NULL, NULL, NULL),
(373, 82, 'Wearing of multiple earrings', NULL, NULL, NULL),
(374, 82, 'Improper Uniform', NULL, NULL, NULL),
(375, 82, 'Improper hairstyle', NULL, NULL, NULL),
(376, 82, 'Improper not wearing/ tampering of ID', NULL, NULL, NULL),
(377, 82, 'Eating inside the laboratories', NULL, NULL, NULL),
(378, 82, 'Using cellphones/ gadgets during class hours', NULL, NULL, NULL),
(379, 82, 'Others', NULL, NULL, NULL),
(380, 82, 'Possession/Use of Liquor/Alcoholic Beverages', NULL, NULL, NULL),
(381, 82, 'Possession/Use of Prohibited Drugs', NULL, NULL, NULL),
(382, 82, 'Smoking within the school vicinity', NULL, NULL, NULL),
(383, 82, 'Gambling', NULL, NULL, NULL),
(384, 82, 'Inflicting/Direct Assault', NULL, NULL, NULL),
(385, 82, 'Theft/Stealing', NULL, NULL, NULL),
(386, 82, 'Cheating/Academic Dishonesty', NULL, NULL, NULL),
(387, 82, 'Cutting Classes', NULL, NULL, NULL),
(388, 82, 'Organizing, planning or joining to any group or fraternity activity', NULL, NULL, NULL),
(389, 82, 'Verbal assault', NULL, NULL, NULL),
(390, 82, 'Vandalism', NULL, NULL, NULL),
(391, 82, 'Stealing', NULL, NULL, NULL),
(392, 83, 'Wearing of earring', NULL, NULL, NULL),
(393, 83, 'Wearing of rubber slippers', NULL, NULL, NULL),
(394, 83, 'Improper wearing of uniform', NULL, NULL, NULL),
(395, 83, 'No ID', NULL, NULL, NULL),
(396, 83, 'Non-prescribed haircut', NULL, NULL, NULL),
(397, 83, 'Wearing of multiple earrings', NULL, NULL, NULL),
(398, 83, 'Organizing, planning or joining to any group or fraternity activity', NULL, NULL, NULL),
(399, 84, 'Organizing, planning or joining to any group or fraternity activity', NULL, NULL, NULL),
(400, 84, 'Verbal assault', NULL, NULL, NULL),
(401, 84, 'Vandalism', NULL, NULL, NULL),
(402, 84, 'Stealing', NULL, NULL, NULL),
(403, 84, 'Improper Uniform', NULL, NULL, NULL),
(404, 84, 'Improper hairstyle', NULL, NULL, NULL),
(405, 84, 'Improper not wearing/ tampering of ID', NULL, NULL, NULL),
(406, 84, 'Eating inside the laboratories', NULL, NULL, NULL),
(407, 84, 'Using cellphones/ gadgets during class hours', NULL, NULL, NULL),
(408, 84, 'Others', NULL, NULL, NULL),
(409, 84, 'Possession/Use of Liquor/Alcoholic Beverages', NULL, NULL, NULL),
(410, 84, 'Possession/Use of Prohibited Drugs', NULL, NULL, NULL),
(411, 84, 'Smoking within the school vicinity', NULL, NULL, NULL),
(412, 84, 'Gambling', NULL, NULL, NULL),
(413, 84, 'Inflicting/Direct Assault', NULL, NULL, NULL),
(414, 84, 'Theft/Stealing', NULL, NULL, NULL),
(415, 84, 'Cheating/Academic Dishonesty', NULL, NULL, NULL),
(416, 84, 'Cutting Classes', NULL, NULL, NULL),
(417, 84, 'Wearing of multiple earrings', NULL, NULL, NULL),
(418, 84, 'Wearing of earring', NULL, NULL, NULL),
(419, 84, 'Non-prescribed haircut', NULL, NULL, NULL),
(420, 84, 'Improper wearing of uniform', NULL, NULL, NULL),
(421, 84, 'Wearing of rubber slippers', NULL, NULL, NULL),
(422, 84, 'No ID', NULL, NULL, NULL),
(423, 85, 'Organizing, planning or joining to any group or fraternity activity', NULL, NULL, NULL),
(424, 85, 'Verbal assault', NULL, NULL, NULL),
(425, 86, 'Organizing, planning or joining to any group or fraternity activity', NULL, NULL, NULL),
(426, 87, 'Organizing, planning or joining to any group or fraternity activity', NULL, NULL, NULL),
(427, 88, 'Organizing, planning or joining to any group or fraternity activity', NULL, NULL, NULL),
(428, 89, 'Organizing, planning or joining to any group or fraternity activity', NULL, NULL, NULL),
(429, 90, 'Organizing, planning or joining to any group or fraternity activity', NULL, NULL, NULL),
(430, 91, 'No ID', NULL, NULL, NULL),
(431, 91, 'Wearing of rubber slippers', NULL, NULL, NULL),
(432, 91, 'Improper wearing of uniform', NULL, NULL, NULL),
(433, 91, 'Non-prescribed haircut', NULL, NULL, NULL),
(434, 91, 'Wearing of earring', NULL, NULL, NULL),
(435, 91, 'Wearing of multiple earrings', NULL, NULL, NULL),
(436, 91, 'Cutting Classes', NULL, NULL, NULL),
(437, 91, 'Cheating/Academic Dishonesty', NULL, NULL, NULL),
(438, 91, 'Theft/Stealing', NULL, NULL, NULL),
(439, 91, 'Inflicting/Direct Assault', NULL, NULL, NULL),
(440, 91, 'Gambling', NULL, NULL, NULL),
(441, 91, 'Smoking within the school vicinity', NULL, NULL, NULL),
(442, 91, 'Possession/Use of Prohibited Drugs', NULL, NULL, NULL),
(443, 91, 'Possession/Use of Liquor/Alcoholic Beverages', NULL, NULL, NULL),
(444, 91, 'Others', NULL, NULL, NULL),
(445, 91, 'Using cellphones/ gadgets during class hours', NULL, NULL, NULL),
(446, 91, 'Eating inside the laboratories', NULL, NULL, NULL),
(447, 91, 'Improper not wearing/ tampering of ID', NULL, NULL, NULL),
(448, 91, 'Improper hairstyle', NULL, NULL, NULL),
(449, 91, 'Improper Uniform', NULL, NULL, NULL),
(450, 91, 'Stealing', NULL, NULL, NULL),
(451, 91, 'Vandalism', NULL, NULL, NULL),
(452, 91, 'Verbal assault', NULL, NULL, NULL),
(453, 91, 'Organizing, planning or joining to any group or fraternity activity', NULL, NULL, NULL),
(454, 92, 'No ID', NULL, NULL, NULL),
(455, 92, 'Wearing of rubber slippers', NULL, NULL, NULL),
(456, 92, 'Improper wearing of uniform', NULL, NULL, NULL),
(457, 93, 'Organizing, planning or joining to any group or fraternity activity', NULL, NULL, NULL),
(458, 93, 'Verbal assault', NULL, NULL, NULL),
(459, 93, 'Vandalism', NULL, NULL, NULL),
(460, 93, 'Stealing', NULL, NULL, NULL),
(461, 93, 'Improper Uniform', NULL, NULL, NULL),
(462, 93, 'Improper hairstyle', NULL, NULL, NULL),
(463, 93, 'Improper not wearing/ tampering of ID', NULL, NULL, NULL),
(464, 94, 'Others', NULL, NULL, NULL),
(465, 95, 'Others', NULL, NULL, NULL),
(466, 96, 'No ID', NULL, NULL, NULL),
(467, 97, 'Improper wearing of uniform', NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Stand-in structure for view `violation_summary`
-- (See below for the actual view)
--
CREATE TABLE `violation_summary` (
`id` int(11)
,`student_id` varchar(50)
,`student_name` varchar(255)
,`offense_count` int(11)
,`recorded_by` varchar(255)
,`recorded_at` timestamp
,`violations` mediumtext
);

-- --------------------------------------------------------

--
-- Table structure for table `violation_types`
--

CREATE TABLE `violation_types` (
  `id` int(11) NOT NULL,
  `violation_name` varchar(255) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `violation_types`
--

INSERT INTO `violation_types` (`id`, `violation_name`, `category`, `is_active`, `created_at`) VALUES
(1, 'No ID', 'Dress Code', 1, '2025-07-22 06:23:33'),
(2, 'Wearing of rubber slippers', 'Dress Code', 1, '2025-07-22 06:23:33'),
(3, 'Improper wearing of uniform', 'Dress Code', 1, '2025-07-22 06:23:33'),
(4, 'Non-prescribed haircut', 'Dress Code', 1, '2025-07-22 06:23:33'),
(5, 'Wearing of earrings', 'Dress Code', 1, '2025-07-22 06:23:33'),
(6, 'Wearing of multiple earrings', 'Dress Code', 1, '2025-07-22 06:23:33'),
(7, 'Using cellphones/ gadgets during class hours.', 'Minor', 1, '2025-07-22 06:23:33'),
(8, 'Eating inside the laboratories', 'Minor', 1, '2025-07-22 06:23:33'),
(9, 'Improper not wearing/ tampering of ID', 'Minor', 1, '2025-07-22 06:23:33'),
(10, 'Improper/tampered ID', 'Minor', 1, '2025-07-22 06:23:33'),
(11, 'Improper hairstyle', 'Minor', 1, '2025-07-22 06:23:33'),
(12, 'Improper Uniform', 'Minor', 1, '2025-07-22 06:23:33'),
(13, 'Stealing', 'Major', 1, '2025-07-22 06:23:33'),
(14, 'Vandalism', 'Major', 1, '2025-07-22 06:23:33'),
(15, 'Verbal assault', 'Major', 1, '2025-07-22 06:23:33'),
(16, 'Organizing, planning or joining to any group or fraternity activity.', 'Major', 1, '2025-07-22 06:23:33'),
(17, 'Organizing/joining fraternity activities', 'Major', 1, '2025-07-22 06:23:33'),
(18, 'Cutting Classes', 'Conduct', 1, '2025-07-22 06:23:33'),
(19, 'Cheating/Academic Dishonesty', 'Conduct', 1, '2025-07-22 06:23:33'),
(20, 'Cheating / Academic Dishonesty', 'Conduct', 1, '2025-07-22 06:23:33'),
(21, 'Theft/Stealing', 'Conduct', 1, '2025-07-22 06:23:33'),
(22, 'Theft / Stealing', 'Conduct', 1, '2025-07-22 06:23:33'),
(23, 'Inflicting/Direct Assault', 'Conduct', 1, '2025-07-22 06:23:33'),
(24, 'Inflicting / Direct Assault', 'Conduct', 1, '2025-07-22 06:23:33'),
(25, 'Gambling', 'Conduct', 1, '2025-07-22 06:23:33'),
(26, 'Smoking within the school vicinity', 'Conduct', 1, '2025-07-22 06:23:33'),
(27, 'Smoking within the school', 'Conduct', 1, '2025-07-22 06:23:33'),
(28, 'Possession/Use of Prohibited Drugs', 'Conduct', 1, '2025-07-22 06:23:33'),
(29, 'Use/Possession of Prohibited Drugs', 'Conduct', 1, '2025-07-22 06:23:33'),
(30, 'Possession/Use of Liquor/Alcoholic Beverages', 'Conduct', 1, '2025-07-22 06:23:33'),
(31, 'Use/Possession of Liquor/Alcohol', 'Conduct', 1, '2025-07-22 06:23:33'),
(32, 'Others', 'Miscellaneous', 1, '2025-07-22 06:23:33'),
(33, 'Wearing of earring', 'Dress Code', 1, '2025-08-31 10:31:47'),
(34, 'Using cellphones/ gadgets during class hours', 'Minor', 1, '2025-08-31 10:31:47'),
(35, 'Organizing, planning or joining to any group or fraternity activity', 'Major', 1, '2025-08-31 10:31:47');

-- --------------------------------------------------------

--
-- Structure for view `student_stats`
--
DROP TABLE IF EXISTS `student_stats`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `student_stats`  AS SELECT `s`.`student_id` AS `student_id`, `s`.`student_name` AS `student_name`, `s`.`year_level` AS `year_level`, `s`.`course` AS `course`, `s`.`section` AS `section`, coalesce(`soc`.`current_offense_count`,0) AS `current_offense_count`, count(`v`.`id`) AS `total_violations` FROM ((`students` `s` left join `student_offense_counts` `soc` on(`s`.`student_id` = `soc`.`student_id`)) left join `violations` `v` on(`s`.`student_id` = `v`.`student_id`)) GROUP BY `s`.`student_id` ;

-- --------------------------------------------------------

--
-- Structure for view `violation_summary`
--
DROP TABLE IF EXISTS `violation_summary`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `violation_summary`  AS SELECT `v`.`id` AS `id`, `v`.`student_id` AS `student_id`, `v`.`student_name` AS `student_name`, `v`.`offense_count` AS `offense_count`, `v`.`recorded_by` AS `recorded_by`, `v`.`recorded_at` AS `recorded_at`, group_concat(`vd`.`violation_type` separator ', ') AS `violations` FROM (`violations` `v` left join `violation_details` `vd` on(`v`.`id` = `vd`.`violation_id`)) GROUP BY `v`.`id` ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `penalty_matrix`
--
ALTER TABLE `penalty_matrix`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `violation_type` (`violation_type`,`offense_count`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `student_id` (`student_id`),
  ADD KEY `idx_student_id` (`student_id`),
  ADD KEY `idx_student_name` (`student_name`);

--
-- Indexes for table `student_violation_offense_counts`
--
ALTER TABLE `student_violation_offense_counts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `student_id` (`student_id`,`violation_type`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `violations`
--
ALTER TABLE `violations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_student_id` (`student_id`),
  ADD KEY `idx_recorded_at` (`recorded_at`);

--
-- Indexes for table `violation_details`
--
ALTER TABLE `violation_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_violation_id` (`violation_id`);

--
-- Indexes for table `violation_types`
--
ALTER TABLE `violation_types`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `violation_name` (`violation_name`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `penalty_matrix`
--
ALTER TABLE `penalty_matrix`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=106;

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `student_violation_offense_counts`
--
ALTER TABLE `student_violation_offense_counts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=343;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `violations`
--
ALTER TABLE `violations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=98;

--
-- AUTO_INCREMENT for table `violation_details`
--
ALTER TABLE `violation_details`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=468;

--
-- AUTO_INCREMENT for table `violation_types`
--
ALTER TABLE `violation_types`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `violations`
--
ALTER TABLE `violations`
  ADD CONSTRAINT `violations_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE;

--
-- Constraints for table `violation_details`
--
ALTER TABLE `violation_details`
  ADD CONSTRAINT `violation_details_ibfk_1` FOREIGN KEY (`violation_id`) REFERENCES `violations` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
